package com.approagency.pharmacy.presentation.components


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.approagency.pharmacy.domain.model.PharmacyItem
import com.approagency.pharmacy.presentation.common.CustomBox
import com.approagency.pharmacy.presentation.common.CustomModalBottomSheet
import com.approagency.pharmacy.presentation.common.Loading
import com.approagency.pharmacy.presentation.viewModel.PharmacyDetailState
import com.approagency.pharmacy.presentation.viewModel.PharmacyState
import com.approagency.pharmacy.presentation.viewModel.PharmacyViewModel
import com.approagency.pharmacy.utils.provinces
import com.vada.caller.ui.theme.LocalDime
import com.vada.caller.ui.theme.dime

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacyBottomSheet(
    viewModel: PharmacyViewModel,
    genericDrugId: String,
    brandIrc: String,
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false, confirmValueChange = { true })


    var selectedProvinceId by remember { mutableStateOf("40") } // پیش‌فرض تهران
    var showProvinceDropdown by remember { mutableStateOf(false) }

    val pharmacyState by viewModel.state.collectAsState()

    // جستجوی اولیه با استان تهران — با تغییر دارو دوباره اجرا می‌شود
    // (در صورت خالی بودن شناسه، ViewModel وضعیت قبلی را پاک و خطا نمایش می‌دهد)
    LaunchedEffect(genericDrugId, brandIrc) {
        viewModel.search(genericDrugId, brandIrc, "40")
    }

    if (isOpen) {
        CustomModalBottomSheet(
            sheetState = sheetState, onDismiss = onDismiss, scope = scope
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .heightIn(min = 400.dp)
                        .padding(MaterialTheme.dime.md),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.dime.xl)
                ) {
                    item {
                        Text(
                            text = "جستجوی دارو در داروخانه‌ها",
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                        )
                    }
                    // عنوان

                    item {
                        ExposedDropdownMenuBox(
                            expanded = showProvinceDropdown,
                            onExpandedChange = { showProvinceDropdown = it }) {
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
                                onDismissRequest = { showProvinceDropdown = false }) {
                                provinces.forEach { province ->
                                    DropdownMenuItem(text = { Text(province.name) }, onClick = {
                                        selectedProvinceId = province.id
                                        showProvinceDropdown = false
                                        // جستجو با استان جدید
                                        viewModel.search(genericDrugId, brandIrc, province.id)
                                    })
                                }
                            }
                        }
                    }
                    // انتخاب استان


                    // نمایش نتایج
                    when (val state = pharmacyState) {
                        PharmacyState.Loading -> {
                            item {
                                Loading(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(400.dp),
                                    message = "در حال جستجو..."
                                )
                            }
                        }

                        is PharmacyState.Success -> {
                            if (state.items.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(400.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "هیچ داروخانه‌ای یافت نشد",
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }

                            } else {
                                items(state.items) { pharmacy ->
                                    PharmacyResultItem(pharmacy = pharmacy, viewModel)
                                }
                                item {
                                    Spacer(modifier = Modifier.height(MaterialTheme.dime.md))
                                    Text(
                                        text = "تعداد کل: ${state.items.size} داروخانه",
                                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(bottom = MaterialTheme.dime.xs)
                                    )
                                }
//                                   LazyColumn(
//                                       verticalArrangement = Arrangement.spacedBy(8.dp),
//                                       modifier = Modifier,
//                                   ) {
//                                       items(state.items) { pharmacy ->
//                                           PharmacyResultItem(pharmacy = pharmacy)
//                                       }
//
//
//                                   }

                            }
                        }

                        is PharmacyState.Error -> {
                            item {
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
                                        Spacer(modifier = Modifier.height(MaterialTheme.dime.sm))
                                        TextButton(
                                            onClick = { viewModel.retry() }) {
                                            Text("تلاش مجدد")
                                        }
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
    pharmacy: PharmacyItem, viewModel: PharmacyViewModel, modifier: Modifier = Modifier
) {
    val dime = LocalDime.current
    var showDetails by remember { mutableStateOf(false) }
    // گرفتن State مخصوص این داروخانه از Map
    val detailStates by viewModel.pharmacyDetailStates.collectAsState()
    val detailState = detailStates[pharmacy.pharmacyUrl] ?: PharmacyDetailState.Idle

    // Reset detail state when item changes
    LaunchedEffect(pharmacy.pharmacyUrl) {
        println(pharmacy.pharmacyUrl)
        showDetails = false
    }

    // Load detail when expanded
    LaunchedEffect(showDetails) {
        if (showDetails) {
            // فقط اگر Idle هست (هنوز لود نشده) درخواست بده
            if (detailState is PharmacyDetailState.Idle) {
                println(pharmacy.pharmacyUrl)
                viewModel.loadPharmacyDetail(pharmacy.pharmacyUrl)
            }
        }
    }
    CustomBox(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dime.md),
            verticalArrangement = Arrangement.spacedBy(dime.xs)
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
            // موقعیت و دکمه اطلاعات تماس
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${pharmacy.province} - ${pharmacy.city}",
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                TextButton(
                    onClick = { showDetails = !showDetails }) {
                    Text(
                        text = if (showDetails) "بستن" else "اطلاعات تماس",
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                }
            }
            if (showDetails) {

                when (detailState) {
                    is PharmacyDetailState.Loading -> {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Loading(size = 24.dp)
                        }
                    }

                    is PharmacyDetailState.Success -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(dime.sm)
                        ) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = MaterialTheme.dime.xs))
                            // آدرس
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(dime.sm)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text =  detailState.detail.address,
                                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                    lineHeight = 18.sp
                                )
                            }

                            // تلفن
                            if (detailState.detail.phone.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(dime.sm)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = detailState.detail.phone,
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    is PharmacyDetailState.Error -> {
                        Text(
                            text = "خطا در دریافت اطلاعات تماس",
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}