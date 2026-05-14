package com.approagency.drug.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.approagency.drug.domain.model.TestGroup
import com.approagency.drug.presentation.common.CustomModalBottomSheet
import com.approagency.drug.presentation.viewModel.LabViewModel
import com.approagency.drug.presentation.viewModel.TestItemState
import com.vada.caller.ui.theme.dime
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestDetailSheet(state: TestItemState, testGroup: TestGroup, onDismiss: () -> Unit) {
    if (state.testItem != null) {
        val testItems by state.testItem.collectAsState(initial = emptyList())

        CustomModalBottomSheet(
            onDismiss = onDismiss,
            content = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.dime.lg),
                        contentPadding = PaddingValues(bottom = MaterialTheme.dime.lg)
                    ) {
                        item {
                            Text(
                                text = testGroup.fname ?: "جزئیات آزمایش",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = MaterialTheme.dime.md)
                            )
                            Text(
                                text = testGroup.ename ?: "N/A (English Name)",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = MaterialTheme.dime.sm)
                            )
                            if (testGroup.detail != null){
                                    Text(testGroup.detail ,  style = MaterialTheme.typography.bodySmall,modifier = Modifier.padding(bottom = MaterialTheme.dime.md) )
                            }



                            HorizontalDivider(Modifier.padding(bottom = MaterialTheme.dime.md))
                        }

                        if (testItems.isEmpty()) {
                            item {

                            }
                        } else {
                            items(testItems) { testItem ->
                                TestItemDetailCard(testItem = testItem)
                            }
                        }


                    }
                }
            }
        )
    }
}

@Composable
fun TestItemDetailCard(testItem: com.approagency.drug.domain.model.TestItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.dime.md)
    ) {
        Text(
            text = testItem.title ?: "بدون عنوان",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (!testItem.normalValue.isNullOrBlank()) {
            Text(
                text = "مقدار نرمال: ${testItem.normalValue}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = MaterialTheme.dime.xs)
            )
        }

        if (!testItem.detail.isNullOrBlank()) {
            Text(
                text = testItem.detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = MaterialTheme.dime.sm)
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = MaterialTheme.dime.md),
            thickness = 0.5.dp
        )
    }
}