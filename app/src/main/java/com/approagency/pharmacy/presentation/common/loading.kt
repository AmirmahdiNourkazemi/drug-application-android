package com.approagency.pharmacy.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vada.caller.ui.theme.dime

/**
 * تنها کامپوزبل لودینگ برنامه — همه‌جا از همین استفاده می‌شود.
 *
 * - بدون [message] فقط یک اسپینر برمی‌گرداند (برای دکمه‌ها، ردیف‌ها و غیره):
 *      Loading()                         // اسپینر ساده
 *      Loading(size = 24.dp)             // اسپینر کوچک‌تر
 *      Loading(color = MaterialTheme.colorScheme.onPrimary)  // روی دکمه‌ی primary
 *
 * - با [message] یک حالت لودینگ وسط‌چین همراه متن می‌سازد (تمام‌صفحه/ناحیه‌ای):
 *      Loading(modifier = Modifier.fillMaxSize(), message = "در حال جستجو...")
 *
 * رنگ پیش‌فرض primary است تا روی پس‌زمینه‌ی معمول دیده شود.
 */
@Composable
fun Loading(
    modifier: Modifier = Modifier,
    message: String? = null,
    size: Dp = 40.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = 3.dp
) {
    if (message.isNullOrBlank()) {
        CircularProgressIndicator(
            modifier = modifier.size(size),
            color = color,
            strokeWidth = strokeWidth
        )
    } else {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    modifier = Modifier.size(size),
                    color = color,
                    strokeWidth = strokeWidth
                )
                Spacer(modifier = Modifier.height(MaterialTheme.dime.md))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
