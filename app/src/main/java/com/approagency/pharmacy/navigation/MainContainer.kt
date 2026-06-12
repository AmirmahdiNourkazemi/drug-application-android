package com.approagency.pharmacy.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.approagency.pharmacy.data.local.AccountState
import com.approagency.pharmacy.data.local.SessionManager
import com.approagency.pharmacy.domain.repository.AuthRepository
import com.approagency.pharmacy.presentation.account.AccountDrawer
import com.approagency.pharmacy.presentation.account.AccountSheet
import com.approagency.pharmacy.presentation.account.AccountSheetController
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val session: SessionManager = koinInject()
    val sheetController: AccountSheetController = koinInject()
    val authRepository: AuthRepository = koinInject()

    val account by session.account.collectAsState()
    val sheetVisible by sheetController.visible.collectAsState()
    val themeMode by session.themeMode.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val appVersion = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull().orEmpty()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AccountDrawer(
                account = account,
                appVersion = appVersion,
                themeMode = themeMode,
                onThemeModeChange = { session.setThemeMode(it) },
                onLogout = {
                    scope.launch {
                        authRepository.logout()
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                AccountAppBar(
                    account = account,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onAccountClick = { sheetController.show() }
                )
            },
            bottomBar = { BottomBar(navController) }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                content()
            }
        }
    }

    if (sheetVisible) {
        AccountSheet(onDismiss = { sheetController.hide() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountAppBar(
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
                TextButton(onClick = onAccountClick) {
                    Text("ورود", textAlign = TextAlign.Center)
                }
            }
        }
    )
}
