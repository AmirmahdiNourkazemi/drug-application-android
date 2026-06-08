package com.approagency.pharmacy.presentation.components.drugdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vada.caller.ui.theme.LocalDime
import com.vada.caller.ui.theme.dime

/**
 * بلوک‌های مشترکِ ظاهری صفحه‌ی جزئیات دارو.
 * این کامپوزبل‌ها در فایل‌های تب (DrugDetailTabs) استفاده می‌شوند.
 */

/** کارت متنی با عنوانِ سربرگ‌دار و بدنه‌ی توضیحات. */
@Composable
internal fun InfoSectionCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current

    DetailCard(modifier = modifier) {
        SectionHeader(title = title)
        Spacer(modifier = Modifier.height(dime.sm))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp,
            textAlign = TextAlign.Justify,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * کارت پایه‌ی صفحه‌ی جزئیات — ظاهر یکدست با بقیه‌ی برنامه:
 * سطح روشن + قاب نازک هم‌رنگ تم، بدون سایه‌ی سنگین.
 */
@Composable
internal fun DetailCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = MaterialTheme.shapes.large
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = shape
            )
            .padding(MaterialTheme.dime.md),
        content = content
    )
}

/** سربرگ بخش با نوار تأکید کوچک کنار عنوان. */
@Composable
internal fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dime.sm)
    ) {
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 18.dp)
                .clip(RoundedCornerShape(dime.xxs))
                .background(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/** حالت «اطلاعاتی ثبت نشده» با آیکن، یکدست در همه‌ی تب‌ها. */
@Composable
internal fun DetailEmptyState(
    text: String,
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dime.xxl, horizontal = dime.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dime.sm)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/** ردیف «برچسب: مقدار» با تایپوگرافی یکدست. */
@Composable
internal fun LabeledRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
    }
}

/** برچسب کوچک رنگی (مثل «پرخطر» / «حیاتی»). */
@Composable
internal fun TagChip(
    text: String,
    container: Color,
    onContainer: Color
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = onContainer,
        modifier = Modifier
            .clip(RoundedCornerShape(MaterialTheme.dime.xs))
            .background(container)
            .padding(horizontal = MaterialTheme.dime.sm, vertical = MaterialTheme.dime.xxs)
    )
}
