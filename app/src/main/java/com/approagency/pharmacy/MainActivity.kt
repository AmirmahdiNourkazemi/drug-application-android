package com.approagency.pharmacy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.approagency.pharmacy.data.local.SessionManager
import com.approagency.pharmacy.domain.repository.AuthRepository
import com.approagency.pharmacy.navigation.AppNavGraph
import com.approagency.pharmacy.ui.theme.DrugTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val session: SessionManager by inject()
    private val authRepository: AuthRepository by inject()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by session.themeMode.collectAsState()
            DrugTheme(themeMode = themeMode) {
                AppNavGraph()
            }
        }
    }

    /**
     * هنگام شروع اپ و هر بار بازگشت به اپ (مثلاً پس از بازگشت از درگاه پرداختِ
     * مایکت/بازار) وضعیت اشتراک از سرور تازه‌سازی می‌شود تا کل اپ به‌روز بماند.
     */
    override fun onResume() {
        super.onResume()
        if (session.isLoggedIn) {
            lifecycleScope.launch { authRepository.refreshStatus() }
        }
    }
}
