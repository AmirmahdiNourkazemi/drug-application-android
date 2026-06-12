package com.approagency.pharmacy.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import com.approagency.pharmacy.data.local.AccountState

/**
 * نوار بالای اپ: دکمه‌ی منو، اطلاعات حساب (موبایل + وضعیت اشتراک) و کنشِ
 * ورود/خرید اشتراک.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountAppBar(
    account: AccountState,
    onMenuClick: () -> Unit,
    onAccountClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "منو"
                )
            }
        },
        title = {
            if (account.isLoggedIn) {
                Column {
                    Text(
                        text = account.mobile ?: account.displayName.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = account.subscriptionTitle ?: "بدون اشتراک",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (account.isSubscribed)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(text = "دارویاب", style = MaterialTheme.typography.titleMedium)
            }
        },
        actions = {
            if (!account.isLoggedIn) {
                ElevatedButton(onClick = onAccountClick) {
                    Text("ورود", textAlign = TextAlign.Center)
                }
            } else if (!account.isSubscribed) {
                ElevatedButton(onClick = onAccountClick) {
                    Text("خرید اشتراک", textAlign = TextAlign.Center)
                }
            }
        }
    )
}
