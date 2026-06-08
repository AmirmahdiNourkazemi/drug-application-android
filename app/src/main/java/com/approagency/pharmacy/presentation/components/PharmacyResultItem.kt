package com.approagency.pharmacy.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.approagency.pharmacy.domain.model.PharmacyItem
import com.approagency.pharmacy.presentation.common.CustomBox
import com.approagency.pharmacy.presentation.common.Loading
import com.approagency.pharmacy.presentation.viewModel.PharmacyDetailState
import com.approagency.pharmacy.presentation.viewModel.PharmacyViewModel
import com.vada.caller.ui.theme.LocalDime
import com.vada.caller.ui.theme.dime

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
        showDetails = false
    }

    // Load detail when expanded
    LaunchedEffect(showDetails) {
        if (showDetails) {
            // فقط اگر Idle هست (هنوز لود نشده) درخواست بده
            if (detailState is PharmacyDetailState.Idle) {
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
                                    text = detailState.detail.address,
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
