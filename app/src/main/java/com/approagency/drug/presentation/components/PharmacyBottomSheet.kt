package com.approagency.drug.presentation.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.approagency.drug.domain.model.PharmacyItem
import com.approagency.drug.presentation.common.CustomBox
import com.approagency.drug.presentation.common.CustomModalBottomSheet
import com.approagency.drug.presentation.viewModel.PharmacyState
import com.approagency.drug.presentation.viewModel.PharmacyViewModel
import com.approagency.drug.utils.provinces
import com.vada.caller.ui.theme.LocalDime
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacyBottomSheet(
    viewModel: PharmacyViewModel,
    genericDrugId: String,
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { true }
    )


    var selectedProvinceId by remember { mutableStateOf("40") } // پیش‌فرض تهران
    var showProvinceDropdown by remember { mutableStateOf(false) }

    val pharmacyState by viewModel.state.collectAsState()

    // جستجوی اولیه با استان تهران
    LaunchedEffect(Unit) {
        if (genericDrugId.isNotBlank()) {
            viewModel.search(genericDrugId, "40")
        }
    }

    if (isOpen) {
        CustomModalBottomSheet(
            sheetState = sheetState,
            onDismiss = onDismiss,
            scope = scope
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // عنوان
                    Text(
                        text = "جستجوی دارو در داروخانه‌ها",
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )

                    // انتخاب استان
                    ExposedDropdownMenuBox(
                        expanded = showProvinceDropdown,
                        onExpandedChange = { showProvinceDropdown = it }
                    ) {
                        OutlinedTextField(
                            value = provinces.find { it.id == selectedProvinceId }?.name
                                ?: "انتخاب استان",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showProvinceDropdown) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = MaterialTheme.shapes.large
                        )

                        ExposedDropdownMenu(
                            expanded = showProvinceDropdown,
                            onDismissRequest = { showProvinceDropdown = false }
                        ) {
                            provinces.forEach { province ->
                                DropdownMenuItem(
                                    text = { Text(province.name) },
                                    onClick = {
                                        selectedProvinceId = province.id
                                        showProvinceDropdown = false
                                        // جستجو با استان جدید
                                        viewModel.search(genericDrugId, province.id)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // نمایش نتایج
                    when (val state = pharmacyState) {
                        PharmacyState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "در حال جستجو...",
                                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                                    )
                                }
                            }
                        }

                        is PharmacyState.Success -> {
                            if (state.items.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "هیچ داروخانه‌ای یافت نشد",
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f),
                                ) {
                                    items(state.items) { pharmacy ->
                                        PharmacyResultItem(pharmacy = pharmacy)
                                    }

                                    item {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "تعداد کل: ${state.items.size} داروخانه",
                                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        is PharmacyState.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = state.message,
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(
                                        onClick = { viewModel.retry() }
                                    ) {
                                        Text("تلاش مجدد")
                                    }
                                }
                            }
                        }

                        PharmacyState.Idle -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun PharmacyResultItem(
    pharmacy: PharmacyItem,
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current

    CustomBox (
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dime.md),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // نام برند
            Text(
                text = pharmacy.brandName,
                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                color = MaterialTheme.colorScheme.primary
            )

            // نام داروخانه
            Text(
                text = pharmacy.pharmacyName,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.labelLarge.fontSize
            )

            // موقعیت
            Text(
                text = "${pharmacy.province} - ${pharmacy.city}",
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}