package com.approagency.pharmacy.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.approagency.pharmacy.data.local.SessionManager
import com.approagency.pharmacy.domain.repository.AuthRepository
import com.approagency.pharmacy.presentation.account.AccountDrawer
import com.approagency.pharmacy.presentation.account.AccountSheet
import com.approagency.pharmacy.presentation.account.AccountSheetController
import com.approagency.pharmacy.presentation.common.ConfirmBottomSheet
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
    var showLogoutConfirm by remember { mutableStateOf(false) }

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
                onLogout = { showLogoutConfirm = true }
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

    if (showLogoutConfirm) {
        ConfirmBottomSheet(
            title = "خروج از حساب",
            message = "آیا می‌خواهید از حساب کاربری خود خارج شوید؟",
            confirmText = "خروج",
            onConfirm = {
                showLogoutConfirm = false
                scope.launch {
                    authRepository.logout()
                    drawerState.close()
                }
            },
            onDismiss = { showLogoutConfirm = false }
        )
    }
}
