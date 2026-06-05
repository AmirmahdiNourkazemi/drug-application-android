package com.approagency.pharmacy.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.approagency.pharmacy.domain.model.TestGroup
import com.approagency.pharmacy.domain.model.TestItem
import com.approagency.pharmacy.presentation.common.CustomModalDialog
import com.approagency.pharmacy.presentation.common.CustomTextFilled
import com.approagency.pharmacy.presentation.common.Loading
import com.approagency.pharmacy.presentation.common.PrimaryButton
import com.approagency.pharmacy.presentation.components.RetryContent
import com.approagency.pharmacy.presentation.components.SearchResultsContent
import com.approagency.pharmacy.presentation.components.TestDetailSheet
import com.approagency.pharmacy.presentation.components.TestGroupItemContent
import com.approagency.pharmacy.presentation.viewModel.LabViewModel
import com.vada.caller.ui.theme.dime
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.koinViewModel

@Composable
fun LabScreen(
    modifier: Modifier = Modifier,
    viewModel: LabViewModel = koinViewModel()
) {
    val testGroupsState by viewModel.testGroups.collectAsState()
    val testItemsState by viewModel.testItems.collectAsState()
    val searchResultsState by viewModel.searchResults.collectAsState()

    val isLoading = testGroupsState.isLoading || testItemsState.isLoading || searchResultsState.isLoading
    val error = testGroupsState.error ?: searchResultsState.error
    val testGroups = testGroupsState.testGroup

    var selectedGroup by remember { mutableStateOf<TestGroup?>(null) }
    var selectedItem by remember { mutableStateOf<TestItem?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    Column(modifier = modifier.fillMaxSize().padding(horizontal = MaterialTheme.dime.md)
        .padding(top = MaterialTheme.dime.md),) {
        CustomTextFilled(
            value = searchText,
            onValueChange = {
                searchText = it
                viewModel.updateSearchQuery(it)
            },
            onSearch = { query ->
                if (query.isNotBlank()) {
                    viewModel.updateSearchQuery(query)
                }
            },
            placeholder = "جستجو در گروه‌ها و آیتم‌های آزمایشگاهی",
            showClearButton = true,
            showSearchButton = true,
            autoSearch = true,
            height = 45
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dime.xs))
        PrimaryButton(
            text = "جستجو",
            height = 40,
            isLoading =false,
            onClick = {}
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dime.xs))
        if (searchResultsState.query.isNotBlank()) {
            // Search Results Section
            SearchResultsContent(
                searchResult = searchResultsState.searchResults,
                isLoading = searchResultsState.isLoading,
                error = searchResultsState.error,
                onGroupClick = { group ->
                    selectedGroup = group
                    viewModel.getItemByGroupId(group.id)
                    showSheet = true
                },
                onItemClick = { item ->
                    selectedItem = item
                    // You can show item detail sheet here
                    // For now, we'll show the parent group
                    viewModel.getItemByGroupId(item.groupId)
                    showSheet = true
                },
                onClearSearch = {
                    searchText = ""
                    viewModel.clearSearch()
                    viewModel.loadTestGroups()
                }
            )
        } else if(error != null){
            RetryContent(modifier = modifier , error = error , onClick = {
                viewModel.loadTestGroups()
            })
        }
        else if (testGroups != null){
            TestGroupsList(
                testGroupsFlow = testGroups,
                viewModel = viewModel,
                onGroupClick = { group ->
                    selectedGroup = group
                    viewModel.getItemByGroupId(group.id)
                    showSheet = true
                }
            )
        }
        else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("هیچ داده‌ای موجود نیست")
            }
        }

    }
    if (isLoading) {
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
    if (showSheet && selectedGroup != null) {
        TestDetailSheet(
            state = testItemsState,
            testGroup = selectedGroup!!,
            onDismiss = { showSheet = false }
        )
    }
}

@Composable
fun TestGroupsList(
    testGroupsFlow: Flow<List<TestGroup>>,
    viewModel: LabViewModel,
    onGroupClick: (TestGroup) -> Unit
) {
    val testGroups by testGroupsFlow.collectAsState(initial = emptyList())

    if (testGroups.isEmpty()) {
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
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                    .fillMaxSize().padding( vertical =MaterialTheme.dime.md )
        ) {
            items(testGroups) { group ->
                TestGroupItemContent(
                    group = group,
                    onClick = { onGroupClick(group) }
                )
            }
        }
    }
}
