package com.approagency.pharmacy.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.approagency.pharmacy.data.local.SessionManager
import com.approagency.pharmacy.domain.model.DrugSearchResult
import com.approagency.pharmacy.navigation.Screen
import com.approagency.pharmacy.presentation.account.AccountSheetController
import com.approagency.pharmacy.presentation.common.CustomTextFilled
import com.approagency.pharmacy.presentation.common.EmptySearchState
import com.approagency.pharmacy.presentation.common.EndOfListIndicator
import com.approagency.pharmacy.presentation.common.ErrorState
import com.approagency.pharmacy.presentation.common.Loading
import com.approagency.pharmacy.presentation.common.LoadingMoreIndicator
import com.approagency.pharmacy.presentation.common.PrimaryButton
import com.approagency.pharmacy.presentation.components.DaroYabSearchResult
import com.approagency.pharmacy.presentation.components.DrugDetailBottomSheet
import com.approagency.pharmacy.presentation.components.PharmacyBottomSheet
import com.approagency.pharmacy.presentation.viewModel.DrugDetailViewModel
import com.approagency.pharmacy.presentation.viewModel.PharmacyViewModel
import com.approagency.pharmacy.presentation.viewModel.SearchState
import com.approagency.pharmacy.presentation.viewModel.SearchViewModel
import com.vada.caller.ui.theme.LocalDime
import com.vada.caller.ui.theme.dime
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


@Composable
fun SearchScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
    drugDetailViewModel: DrugDetailViewModel = koinViewModel(),
    pharmacyViewModel: PharmacyViewModel = koinViewModel(),
) {
    val dime = LocalDime.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val sheetController: AccountSheetController = koinInject()
    val session: SessionManager = koinInject()
    val account by session.account.collectAsState()
    val remainingFree by viewModel.remainingFreeSearches.collectAsState()
    val searchText = viewModel.searchText
    val state by viewModel.searchState.collectAsState()
    val lazyListState = rememberLazyListState()
    var selectedDrugUrl by remember { mutableStateOf<String?>(null) }

    var showPharmacyBottomSheet by remember { mutableStateOf(false) }
    var selectedDrugForPharmacy by remember { mutableStateOf<DrugSearchResult?>(null) }

    // Auto-search for testing (remove in production)
//    LaunchedEffect(Unit) {
//        if (searchText.isEmpty()) {
//            searchText = "انتی"
//            viewModel.searchDrugs(searchText)
//        }
//    }
    if (selectedDrugUrl != null) {
        DrugDetailBottomSheet(
            detailUrl = selectedDrugUrl,
            onDismiss = { selectedDrugUrl = null },
            viewModel = drugDetailViewModel
        )
    }
    // Detect when user scrolls to the bottom to load more
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && state is SearchState.Success) {
                    val successState = state as SearchState.Success
                    val totalItems = successState.drugs.size
                    // Load more when user is 3 items from the end
                    if (lastVisibleIndex >= totalItems - 3 &&
                        successState.hasMorePages &&
                        !successState.isLoadingMore) {
                        viewModel.loadNextPage()
                    }
                }
            }
    }

    Column(modifier = modifier
        .fillMaxSize()
        .padding(horizontal = MaterialTheme.dime.md)
        .padding(top = MaterialTheme.dime.md)) {
        // Search input
        CustomTextFilled(
            value = searchText,
            onValueChange = { viewModel.updateSearchText(it) },
            onSearch = { query ->
                if (query.isNotBlank()) {
                    keyboardController?.hide()
                    viewModel.searchDrugs(searchText)
                }
            },
            placeholder = "جستجوی دارو",
            showClearButton = true,
            showSearchButton = true,
            autoSearch = true,
            height = 45
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dime.xs))
        PrimaryButton(
            text = "جستجو",
            height = 40,
            isLoading = state is SearchState.Loading,
            onClick = {
                if (searchText.isNotBlank()) {
                    viewModel.searchDrugs(searchText)
                }
            }
        )

        // شمارش جستجوهای رایگانِ باقی‌مانده برای کاربرانِ بدون اشتراک
        if (!account.isSubscribed) {
            Spacer(modifier = Modifier.height(MaterialTheme.dime.xs))
            Text(
                text = "جستجوی رایگان باقی‌مانده: $remainingFree",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dime.md))

//        PrimaryButton(
//            onClick = {
//                session.testForIncrease()
//            } ,
//            text = "",
//            isLoading = false
//        )

        if (showPharmacyBottomSheet && selectedDrugForPharmacy != null) {
            // درخواست بر اساس نوع صفحه فرق می‌کند:
            //  - صفحات برند (/B-...): شناسه باید به‌عنوان brandIrc ارسال شود
            //  - صفحات ژنریک (/G-...): شناسه به‌عنوان genericDrugId ارسال می‌شود
            // (genericId از قبل پارس شده و در هر دو حالت همان عدد را نگه می‌دارد)
            val drug = selectedDrugForPharmacy!!
            val isBrand = drug.detailPageUrl.contains("/B-")
            val genericId = if (isBrand) "0" else drug.genericId
            val brandIrc = if (isBrand) drug.genericId else "0"

            PharmacyBottomSheet(
                viewModel = pharmacyViewModel,
                genericDrugId = genericId,
                brandIrc = brandIrc,
                isOpen = showPharmacyBottomSheet,
                onDismiss = {
                    showPharmacyBottomSheet = false
                    selectedDrugForPharmacy = null
                }
            )
        }
        // Results area
        when (val currentState = state) {
            is SearchState.Loading -> {
                Loading(
                    modifier = Modifier.fillMaxSize(),
                    message = "در حال جستجو..."
                )
            }

            is SearchState.LoadingMore -> {
                // Show existing items with loading indicator at bottom
                LazyColumn(state = lazyListState , modifier = modifier.padding(vertical = MaterialTheme.dime.xs)) {
                    items(currentState.currentItems) { drug ->
                        DaroYabSearchResult(
                            drug = drug,
                            onClickDetail = { selectedDrug ->
                                navController.navigate(
                                    Screen.DrugDetail.createRoute(
                                        selectedDrug.detailPageUrl
                                    )
                                )
                            },
                            onClickDrugStore = { selectedDrug ->
                                selectedDrugForPharmacy = selectedDrug
                                showPharmacyBottomSheet = true
                            }
                        )
                    }
                    item {
                        LoadingMoreIndicator()
                    }
                }
            }

            is SearchState.Success -> {
                if (currentState.drugs.isEmpty()) {
                    EmptySearchState(onRetry = { viewModel.retryLastSearch() })
                } else {
                    LazyColumn(state = lazyListState) {
                        items(currentState.drugs) { drug ->
                            DaroYabSearchResult(
                                drug = drug,
                                onClickDetail = { selectedDrug ->
                                    navController.navigate(
                                        Screen.DrugDetail.createRoute(
                                            selectedDrug.detailPageUrl
                                        )
                                    )
                                },
                                onClickDrugStore = { selectedDrug ->
                                    selectedDrugForPharmacy = selectedDrug
                                    showPharmacyBottomSheet = true
                                }
                            )
                        }

                        // Show loading indicator at bottom when loading more
                        if (currentState.isLoadingMore) {
                            item {
                                LoadingMoreIndicator()
                            }
                        }

                        // Show end of list indicator
                        if (!currentState.hasMorePages && currentState.drugs.isNotEmpty()) {
                            item {
                                EndOfListIndicator()
                            }
                        }
                    }
                }
            }

            is SearchState.Error -> {
                ErrorState(
                    message = currentState.message,
                    onRetry = { viewModel.retryLastSearch() }
                )
            }

            SearchState.RequireLogin -> {
                SearchGatePrompt(
                    message = "سهمیه‌ی جستجوی رایگان شما به پایان رسیده است. برای ادامه وارد شوید.",
                    buttonText = "ورود",
                    onClick = { sheetController.show() }
                )
            }

            SearchState.RequireSubscription -> {
                SearchGatePrompt(
                    message = "برای جستجوی نامحدودِ دارو، اشتراک ویژه تهیه کنید.",
                    buttonText = "تهیه اشتراک",
                    onClick = { sheetController.show() }
                )
            }

            SearchState.Idle -> {
                EmptySearchState(onRetry = {})
            }
        }
    }
}

@Composable
private fun SearchGatePrompt(
    message: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.dime.lg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dime.lg))
        Box(modifier = Modifier.fillMaxWidth()) {
            PrimaryButton(
                text = buttonText,
                isLoading = false,
                onClick = onClick
            )
        }
    }
}