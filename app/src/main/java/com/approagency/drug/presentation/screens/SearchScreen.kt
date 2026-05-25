package com.approagency.drug.presentation.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.approagency.drug.domain.model.DaroYabSearchResult
import com.approagency.drug.domain.model.DrugSearchResult
import com.approagency.drug.presentation.common.CustomTextFilled
import com.approagency.drug.presentation.common.EmptySearchState
import com.approagency.drug.presentation.common.EndOfListIndicator
import com.approagency.drug.presentation.common.ErrorState
import com.approagency.drug.presentation.common.LoadingMoreIndicator
import com.approagency.drug.presentation.common.PrimaryButton
import com.approagency.drug.presentation.components.DaroYabSearchResult
import com.approagency.drug.presentation.components.PaginationControls
import com.approgency.drug.presentation.viewModel.SearchState
import com.approgency.drug.presentation.viewModel.SearchViewModel
import com.vada.caller.ui.theme.LocalDime
import com.vada.caller.ui.theme.dime
import kotlinx.coroutines.flow.collect
import org.koin.androidx.compose.koinViewModel


@Composable
fun SearchScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel()
) {
    val dime = LocalDime.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var searchText by remember { mutableStateOf("") }
    val state by viewModel.searchState.collectAsState()
    val lazyListState = rememberLazyListState()

    // Auto-search for testing (remove in production)
    LaunchedEffect(Unit) {
        if (searchText.isEmpty()) {
            searchText = "انتی"
            viewModel.searchDrugs(searchText)
        }
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

    Column(modifier = modifier.fillMaxSize().padding(dime.lg)) {
        // Search input
        CustomTextFilled(
            value = searchText,
            onValueChange = { searchText = it },
            onSearch = { query ->
                if (query.isNotBlank()) {
                    keyboardController?.hide()
                    viewModel.searchDrugs(searchText)
                }
            },
            placeholder = "جستجوی دارو",
            showClearButton = true,
            showSearchButton = true,
            autoSearch = true, // Set to true if you want search while typing
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


        // Results area
        when (val currentState = state) {
            is SearchState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "در حال جستجو...",
                            modifier = Modifier.padding(top = dime.md),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            is SearchState.LoadingMore -> {
                // Show existing items with loading indicator at bottom
                LazyColumn(state = lazyListState) {
                    items(currentState.currentItems) { drug ->
                        DaroYabSearchResult(
                            drug = drug,
                            onClick = { selectedDrug ->
                                // Navigate to drug detail
                                // navController.navigate("drug_detail/${selectedDrug.genericId}")
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
                                onClick = { selectedDrug ->
                                    // Navigate to drug detail
                                    // navController.navigate("drug_detail/${selectedDrug.genericId}")
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

            SearchState.Idle -> {
                EmptySearchState(onRetry = {})
            }
        }
    }
}