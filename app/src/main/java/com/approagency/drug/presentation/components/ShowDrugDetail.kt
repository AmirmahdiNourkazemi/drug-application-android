package com.approagency.drug.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Import items if you were to use it for lists within LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import com.approagency.drug.data.dto.DrugDetail // Import DrugDetail directly
import com.approagency.drug.data.dto.DrugModels
import com.approagency.drug.presentation.common.CustomModalBottomSheet
import com.approagency.drug.presentation.viewModel.DrugDetailState
import com.approagency.drug.presentation.viewModel.HomeViewModel
import com.vada.caller.ui.theme.dime // Assuming this is your custom spacing/dimension object

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DrugDetailSheet(
    viewModel: HomeViewModel,
    state: DrugDetailState // Assuming you pass the state containing the detail
) {
    val detail: DrugDetail? = state.drugDetail?.getOrNull()?.data

    // Only show the sheet if detail is not null
    if (detail != null) {
        CustomModalBottomSheet(
            onDismiss = {
                viewModel.closeDetail()
            },
            content = {
                // Use a LazyColumn for scrollable content inside the sheet

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.dime.lg),
                        contentPadding = PaddingValues(bottom = MaterialTheme.dime.lg) // Add padding to the bottom of the content for better scrolling
                    ) {
                        // --- 1. Main Title ---
                        item { // Use item {} for single composables within LazyColumn
                            Text(
                                text = detail.nam_fa ?: "جزئیات دارو",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = MaterialTheme.dime.md)
                            )
                            Text(
                                text = detail.nam_en ?: "N/A (English Name)",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = MaterialTheme.dime.lg)
                            )

                            // Divider for visual separation
                            HorizontalDivider(Modifier.padding(bottom = MaterialTheme.dime.md))
                        }

                        // --- 2. Key Groupings ---
                        // Goroh Darmani (Pharmacological Group)
                        detail.goroh_darmani?.let { group ->
                            item {
                                DetailRow(
                                    title = "گروه درمانی",
                                    value = group.nam_fa ?: "نامشخص"
                                )
                            }
                            group.nam_en?.let { enName ->
                                item {
                                    DetailRow(
                                        title = "Group (EN)",
                                        value = enName
                                    )
                                }
                            }
                        }

                        // Goroh Darmanei (Therapeutic Group)
                        detail.goroh_daroei?.let { group ->
                            item {
                                DetailRow(
                                    title = "گروه دارویی",
                                    value = group.nam ?: "نامشخص"
                                )
                            }
                        }

                        // Goroh Farmakologic (Pharmacological Class)
                        detail.goroh_farmakologic_cod?.let { cod ->
                            // NOTE: You might need a separate API call or mapping here
                            // if cod needs to be translated to a name. For now, we show the code.
                            item {
                                DetailRow(
                                    title = "کلاس فارماکولوژیک",
                                    value = cod.toString()
                                )
                            }
                        }

                        item { // Add a divider as a separate item
                            HorizontalDivider(Modifier.padding(vertical = MaterialTheme.dime.md))
                        }

                        // --- 3. Usage & Mechanism xw---
                        detail.mavaredmasraf?.let { usage ->
                            item {
                                DetailSection(title = "موارد مصرف", content = usage)
                            }
                        }

                        detail.mekanismtasir?.let { mechanism ->
                            item {
                                DetailSection(title = "مکانیسم اثر", content = mechanism)
                            }
                        }

                        // --- 4. Warnings & Storage ---
                        detail.hoshdar?.let { warning ->
                            item {
                                DetailSection(
                                    title = "هشدارها",
                                    content = warning,
                                    isWarning = true
                                )
                            }
                        }

                        detail.sharayetnegahdari?.let { storage ->
                            item {
                                DetailSection(
                                    title = "شرایط نگهداری",
                                    content = storage
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

// --- Helper Composables for Clean Code ---

@Composable
fun DetailRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.dime.sm),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Right
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            // Ensure text takes up available space and doesn't push other elements.
            // Using weight(1f, fill = false) is good for aligning to the end if there's space.
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

@Composable
fun DetailSection(
    title: String,
    content: String,
    isWarning: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.dime.md)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = MaterialTheme.dime.sm)
        )
        // Use a text block that respects line breaks (usually done with \n)
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            // Consider adding softWrap = true if content might overflow
            // softWrap = true
        )
    }
    // Add divider after each section for better visual separation
    HorizontalDivider(Modifier.padding(top = MaterialTheme.dime.sm))
}

// Make sure your DrugDetailState in ViewModel looks something like this:
// data class DrugDetailState(
//     val isLoading: Boolean = false,
//     val drugDetail: DrugModels? = null, // Changed to DrugModels? to match data access
//     val isDetailVisible: Boolean = false,
//     val error: String? = null
// )
