package com.approagency.drug.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.approagency.drug.domain.model.DrugSearchParams
import com.approagency.drug.presentation.common.PrimaryButton
import com.approagency.drug.presentation.viewModel.DrugSearchState
import com.approagency.drug.presentation.viewModel.HomeViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun HomeScreen(
    modifier: Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    HomeContent(
        state = state,
        onSearch = { value -> viewModel.searchDrugs(DrugSearchParams(query = value)) },
        onRetry = {
            viewModel.clearError()
        },
        onDrugClick = { drugId -> println(drugId) }
    )
}

@Composable
fun HomeContent(
    state: DrugSearchState,
    onSearch: (String) -> Unit,
    onRetry: () -> Unit,
    onDrugClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var search by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
                horizontalAlignment = Alignment.Start
    ) {
        Column {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                OutlinedTextField(
                    value = search,
                    shape = MaterialTheme.shapes.large,
                    onValueChange = { value ->
                        search = value
                    },
                    label = { Text("جستجوی دارو") },
                    modifier = Modifier.fillMaxWidth(),

                    trailingIcon = {
                        IconButton(onClick = {
                            if (search.isNotBlank()) {
                                onSearch(search)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    }
                )
            }
            PrimaryButton(
                text = "جستجو",
                height = 40,

                isLoading = state.isLoading,
                onClick = {
                    if (search.isNotBlank()) {
                        onSearch(search)
                    }
                }
            )
        }
    }
}