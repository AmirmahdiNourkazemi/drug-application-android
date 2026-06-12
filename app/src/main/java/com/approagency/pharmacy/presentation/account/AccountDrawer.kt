package com.approagency.pharmacy.presentation.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.approagency.pharmacy.data.local.AccountState
import com.approagency.pharmacy.domain.model.ThemeMode
import com.vada.caller.ui.theme.dime

/**
 * کشوی کناری حساب کاربری با طراحی بهبودیافته:
 *  - سربرگ با آواتار، نام/موبایل و نشانِ وضعیت اشتراک
 *  - کارت اطلاعات (موبایل، اشتراک، تاریخ انقضا، نسخه‌ی برنامه)
 *  - انتخاب حالت نمایش (سیستم/روشن/تاریک)
 *  - دکمه‌ی خروج
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDrawer(
    account: AccountState,
    appVersion: String,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(280.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.dime.lg, vertical = MaterialTheme.dime.xl)
            ) {
                Header(account)

                Spacer(Modifier.height(MaterialTheme.dime.xl))

                // کارت اطلاعات حساب
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(MaterialTheme.dime.md)) {
                        InfoRow(
                            icon = Icons.Filled.Phone,
                            label = "شماره موبایل",
                            value = account.mobile ?: "-"
                        )
                        InfoRow(
                            icon = Icons.Filled.WorkspacePremium,
                            label = "اشتراک",
                            value = account.subscriptionTitle ?: "بدون اشتراک",
                            highlight = account.isSubscribed
                        )
                        if (account.isSubscribed) {
                            InfoRow(
                                icon = Icons.Filled.CalendarMonth,
                                label = "تاریخ انقضا",
                                value = account.subscriptionExpireAt ?: "-"
                            )
                        }
                        InfoRow(
                            icon = Icons.Filled.Info,
                            label = "نسخه برنامه",
                            value = appVersion.ifBlank { "-" }
                        )
                    }
                }

                Spacer(Modifier.height(MaterialTheme.dime.xl))

                // انتخاب حالت نمایش
                Text(
                    text = "حالت نمایش",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(MaterialTheme.dime.sm))
                ThemeModeSelector(selected = themeMode, onSelect = onThemeModeChange)

                Spacer(Modifier.weight(1f))

                if (account.isLoggedIn) {
                    OutlinedButton(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.size(MaterialTheme.dime.sm))
                        Text(
                            text = "خروج از حساب",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(account: AccountState) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(Modifier.size(MaterialTheme.dime.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = account.displayName?.takeIf { it.isNotBlank() }
                    ?: account.mobile
                    ?: "کاربر مهمان",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(MaterialTheme.dime.xs))
            SubscriptionBadge(isSubscribed = account.isSubscribed)
        }
    }
}

@Composable
private fun SubscriptionBadge(isSubscribed: Boolean) {
    val container = if (isSubscribed)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surfaceContainerHighest
    val content = if (isSubscribed)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Surface(color = container, shape = CircleShape) {
        Text(
            text = if (isSubscribed) "اشتراک فعال" else "بدون اشتراک",
            style = MaterialTheme.typography.labelSmall,
            color = content,
            modifier = Modifier.padding(
                horizontal = MaterialTheme.dime.sm,
                vertical = MaterialTheme.dime.xxs
            )
        )
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.dime.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.size(MaterialTheme.dime.sm))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (highlight)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeModeSelector(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit
) {
    val options = listOf(
        Triple(ThemeMode.SYSTEM, "سیستم", Icons.Filled.BrightnessAuto),
        Triple(ThemeMode.LIGHT, "روشن", Icons.Filled.LightMode),
        Triple(ThemeMode.DARK, "تاریک", Icons.Filled.DarkMode)
    )
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (mode, label, icon) ->
            SegmentedButton(
                selected = selected == mode,
                onClick = { onSelect(mode) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                label = { Text(label, style = MaterialTheme.typography.labelMedium) }
            )
        }
    }
}
