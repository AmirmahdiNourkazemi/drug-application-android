package com.approagency.drug.presentation.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.approagency.drug.domain.model.PharmacyItem
import com.approagency.drug.presentation.common.CustomModalBottomSheet
import com.approagency.drug.presentation.viewModel.PharmacyState
import com.approagency.drug.presentation.viewModel.PharmacyViewModel
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
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )

    // لیست استان‌ها (همان لیست HTML)
    val provinces = listOf(
        Province("0", "همه استان ها"),
        Province("1", "اصفهان"),
        Province("40", "تهران"),
        Province("137", "آذربایجان شرقی"),
        Province("138", "آذربایجان غربی"),
        Province("139", "اردبیل"),
        Province("140", "البرز"),
        Province("141", "ایلام"),
        Province("142", "بوشهر"),
        Province("143", "چهارمحال و بختیاری"),
        Province("144", "خراسان جنوبی"),
        Province("145", "خراسان رضوی"),
        Province("146", "خراسان شمالی"),
        Province("147", "خوزستان"),
        Province("148", "زنجان"),
        Province("149", "سمنان"),
        Province("150", "سیستان و بلوچستان"),
        Province("151", "فارس"),
        Province("152", "قزوین"),
        Province("153", "قم"),
        Province("154", "کردستان"),
        Province("155", "کرمان"),
        Province("156", "کرمانشاه"),
        Province("157", "کهگیلویه و بویراحمد"),
        Province("158", "گلستان"),
        Province("159", "گیلان"),
        Province("160", "لرستان"),
        Province("161", "مازندران"),
        Province("162", "مرکزی"),
        Province("163", "هرمزگان"),
        Province("164", "همدان"),
        Province("165", "یزد")
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
                        value = provinces.find { it.id == selectedProvinceId }?.name ?: "انتخاب استان",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showProvinceDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = MaterialTheme.shapes.small
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
                                modifier = Modifier.heightIn(max = 400.dp)
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

@Composable
fun PharmacyResultItem(
    pharmacy: PharmacyItem,
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                color = MaterialTheme.colorScheme.primary
            )

            // نام داروخانه
            Text(
                text = pharmacy.pharmacyName,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize
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

data class Province(
    val id: String,
    val name: String
)