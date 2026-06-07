package com.approagency.pharmacy.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.approagency.pharmacy.domain.model.TestGroup
import com.approagency.pharmacy.domain.model.TestItem
import com.approagency.pharmacy.presentation.common.CustomBox
import com.approagency.pharmacy.presentation.common.Loading
import com.approagency.pharmacy.presentation.viewModel.SearchResult
import com.vada.caller.ui.theme.dime
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection

@Composable
fun SearchResultsContent(
    searchResult: SearchResult?,
    isLoading: Boolean,
    error: String?,
    onGroupClick: (TestGroup) -> Unit,
    onItemClick: (TestItem) -> Unit,
    onClearSearch: () -> Unit
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Loading(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(MaterialTheme.dime.sm))
                    Text("در حال جستجو...")
                }
            }
        }

        error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "خطا در جستجو",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = MaterialTheme.dime.sm)
                    )
                }
            }
        }

        searchResult != null -> {
            val groups = searchResult.groups
            val items = searchResult.items

            if (groups.isEmpty() && items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "نتیجه‌ای یافت نشد",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(MaterialTheme.dime.sm))
                        Text(
                            text = "لطفاً عبارت دیگری را جستجو کنید",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = MaterialTheme.dime.md),
                    horizontalAlignment = AbsoluteAlignment.Right

                ) {
                    if (groups.isNotEmpty()) {
                        item {
                            Text(
                                text = "گروه‌های آزمایشگاهی (${groups.size})",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    textAlign = TextAlign.Center
                                ),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = MaterialTheme.dime.md)
                            )
                        }

                        items(groups) { group ->
                            TestGroupItemContent(
                                group = group,
                                onClick = { onGroupClick(group) }
                            )
                        }

                        if (items.isNotEmpty()) {
                            item {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = MaterialTheme.dime.md)
                                )
                                Text(
                                    text = "آیتم‌های آزمایشگاهی (${items.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = MaterialTheme.dime.md)
                                )
                            }
                        }
                    }

                    items(items) { item ->
                        TestItemSearchResult(
                            testItem = item,
                            onClick = { onItemClick(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TestItemSearchResult(
    testItem: TestItem,
    onClick: () -> Unit
) {
    CustomBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = MaterialTheme.dime.sm)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            }
    ) {
        Column(
            modifier = Modifier.padding(all = MaterialTheme.dime.lg),
            horizontalAlignment = AbsoluteAlignment.Right
        ) {
            Text(
                text = testItem.title ?: "بدون عنوان",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )

            if (!testItem.normalValue.isNullOrBlank()) {
                Text(
                    text = "مقدار نرمال: ${testItem.normalValue}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDirection = TextDirection.Rtl,
                        textAlign = TextAlign.Right
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = MaterialTheme.dime.xs)
                )
            }

            if (!testItem.detail.isNullOrBlank()) {
                Text(
                    text = testItem.detail,
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDirection = TextDirection.Rtl,
                        textAlign = TextAlign.Right
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = MaterialTheme.dime.sm)
                )
            }
        }
    }
}

