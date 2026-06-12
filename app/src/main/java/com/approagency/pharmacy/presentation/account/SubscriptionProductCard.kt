package com.approagency.pharmacy.presentation.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.approagency.pharmacy.domain.model.SubscriptionProduct
import com.approagency.pharmacy.presentation.common.PrimaryButton
import com.approagency.pharmacy.presentation.common.shimmer
import com.vada.caller.ui.theme.dime

/** کارت یک محصول اشتراک به‌همراه دکمه‌ی خرید. */
@Composable
fun SubscriptionProductCard(
    product: SubscriptionProduct,
    isPurchasing: Boolean,
    onBuy: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(MaterialTheme.dime.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(product.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${formatPrice(product.price)} تومان",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (!product.description.isNullOrBlank()) {
                Spacer(Modifier.height(MaterialTheme.dime.xs))
                Text(
                    product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(MaterialTheme.dime.md))
            PrimaryButton(text = "خرید", height = 44, isLoading = isPurchasing, onClick = onBuy)
        }
    }
}

/** جای‌گیرنده‌ی shimmer برای کارت محصول هنگام بارگذاری. */
@Composable
fun SubscriptionProductCardSkeleton() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(MaterialTheme.dime.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    Modifier
                        .width(120.dp)
                        .height(18.dp)
                        .shimmer()
                )
                Box(
                    Modifier
                        .width(72.dp)
                        .height(18.dp)
                        .shimmer()
                )
            }
            Spacer(Modifier.height(MaterialTheme.dime.md))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .shimmer(shape = MaterialTheme.shapes.medium)
            )
        }
    }
}

/** قالب‌بندی قیمت با جداکننده‌ی هزارگان. */
private fun formatPrice(price: Long): String =
    price.toString().reversed().chunked(3).joinToString(",").reversed()
