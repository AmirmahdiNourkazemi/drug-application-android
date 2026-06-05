package com.approagency.pharmacy.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.approagency.pharmacy.domain.model.DrugSearchParams
import com.approagency.pharmacy.presentation.common.CustomModalDialog
import com.approagency.pharmacy.presentation.common.Loading
import com.approagency.pharmacy.presentation.components.DarmanContent
import com.approagency.pharmacy.presentation.components.DrugDetailSheet
import com.approagency.pharmacy.presentation.components.DrugListContent
import com.approagency.pharmacy.presentation.components.EmptyStateContent
import com.approagency.pharmacy.presentation.viewModel.CombineState
import com.approagency.pharmacy.presentation.viewModel.HomeEvent
import com.approagency.pharmacy.presentation.viewModel.HomeViewModel
import com.vada.caller.ui.theme.dime
import kotlinx.coroutines.flow.SharedFlow
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController : NavController,
    modifier: Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    if (state.drugDetailState.isDetailVisible) {
        DrugDetailSheet(
            viewModel = viewModel,
            state = state.drugDetailState
        )
    }
    HomeContent(
        modifier = modifier,
        state = state,
        viewModelEvent = viewModel.event,
        onSearch = { value ->
            if (value is Int) {
                viewModel.searchDrugs(DrugSearchParams(healGroup = value))
            } else if (value is String) {
                viewModel.searchDrugs(DrugSearchParams(query = value))
            }
            keyboardController?.hide()
            focusManager.clearFocus()},
        onRetry = {
            viewModel.getDarmani()
        },
        onDrugClick = { drugId -> viewModel.drugDetail(drugId , allDrugs = state.drugSearchState.drugsData) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: CombineState,
    viewModelEvent: SharedFlow<HomeEvent>,
    onSearch: (Any) -> Unit,
    onRetry: () -> Unit,
    onDrugClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxSize().padding(horizontal = MaterialTheme.dime.md)
            .padding(top = MaterialTheme.dime.md),
                horizontalAlignment = Alignment.Start,
    ) {
        Column  (
            verticalArrangement = Arrangement.Center
        ){
//            CustomTextFilled(
//                value = searchText,
//                onValueChange = { searchText = it },
//                onSearch = { query ->
//                    if (query.isNotBlank()) {
//                        onSearch(query)
//                    }
//                },
//                placeholder = "جستجوی دارو",
//                showClearButton = true,
//                showSearchButton = true,
//                autoSearch = false, // Set to true if you want search while typing
//                height = 45
//            )
//            Spacer(modifier = Modifier.height(MaterialTheme.dime.xs))
//            PrimaryButton(
//                text = "جستجو",
//                height = 40,
//                isLoading = state.drugSearchState.isLoading,
//                onClick = {
//                    if (searchText.isNotBlank()) {
//                        onSearch(searchText)
//                    }
//                }
//            )
//            Spacer(modifier = Modifier.height(MaterialTheme.dime.xs))
            Box(modifier = Modifier.fillMaxSize()) {

                // Always draw the data if available
                if (state.drugSearchState.drugsData != null && !state.showDarmanList) {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                           TextButton(
                               onClick = {
                                   onRetry()
                               }
                           ) {
                               Text("مشاهده تمامی گروه های دارویی")
                               Icon(
                                   imageVector = Icons.Default.KeyboardArrowRight,
                                   contentDescription = "back"
                               )
                               Spacer(modifier = Modifier.width(MaterialTheme.dime.sm))
                           }
                        }
                        DrugListContent(
                            drugsData = state.drugSearchState.drugsData,
                            onDrugClick = onDrugClick
                        )
                    }
                }
               else if (state.darmanState.getDarmani != null ){
                    Column {

                           Row(
                               modifier = Modifier.fillMaxWidth(),
                               horizontalArrangement = Arrangement.End
                           ) {
                               Text("لیست گروه های دارویی" , style = MaterialTheme.typography.labelLarge.copy(
                                   color = MaterialTheme.colorScheme.primary
                               ) ,modifier= Modifier.padding(MaterialTheme.dime.md))
                           }

                        DarmanContent(
                            darmanData = state.darmanState.getDarmani,
                            onDarmanClick = { value ->
                                onSearch(value)
                            },
                            onRetryClick = {
                                onRetry()
                            }
                        )
                    }
                } else {
                    EmptyStateContent()
                }



                // Draw the loading dialog on top if needed
                if (state.drugSearchState.isLoading || state.drugDetailState.isLoading || state.darmanState.isLoading) {
                    CustomModalDialog(
                        onDismissRequest = { /* maybe disable dismiss while loading */ },
                        content = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Loading(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(MaterialTheme.dime.sm))
                                Text("در حال جستجو..." , textAlign = TextAlign.Right)
                            }
                        },
//                        modifier = Modifier
//                            .matchParentSize()
//                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                    )
                }
            }


        }
    }
}





