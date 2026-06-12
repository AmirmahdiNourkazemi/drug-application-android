package com.approagency.pharmacy

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.approagency.pharmacy.data.local.SessionManager
import com.approagency.pharmacy.domain.repository.AuthRepository
import com.approagency.pharmacy.navigation.AppNavGraph
import com.approagency.pharmacy.presentation.account.OtpAutoFillBus
import com.approagency.pharmacy.ui.theme.DrugTheme
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val session: SessionManager by inject()
    private val authRepository: AuthRepository by inject()
    private val otpAutoFillBus: OtpAutoFillBus by inject()

    private var otpSmsReceiver: BroadcastReceiver? = null

    // نتیجه‌ی دیالوگ رضایتِ خواندن پیامک: کد ۵ رقمی استخراج و به شیت ورود تحویل می‌شود.
    private val smsConsentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val message = result.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                val code = Regex("\\b\\d{5}\\b").find(message ?: "")?.value
                code?.let { otpAutoFillBus.submit(it) }
            }
        }

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

    // ---------- خودکارپُرکُنِ کد پیامک (SMS User Consent API) ----------

    /** آغاز گوش‌دادن به پیامکِ کد؛ کدِ یافت‌شده از طریق [OtpAutoFillBus] تحویل می‌شود. */
    fun startOtpAutofill() {
        SmsRetriever.getClient(this).startSmsUserConsent(null)
        if (otpSmsReceiver != null) return
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, received: Intent?) {
                if (received?.action != SmsRetriever.SMS_RETRIEVED_ACTION) return
                val extras = received.extras ?: return
                val status = extras.get(SmsRetriever.EXTRA_STATUS) as? Status ?: return
                if (status.statusCode != CommonStatusCodes.SUCCESS) return
                val consentIntent: Intent? =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT, Intent::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT)
                    }
                consentIntent?.let { runCatching { smsConsentLauncher.launch(it) } }
            }
        }
        otpSmsReceiver = receiver
        // این برودکست را Google Play services با مجوز SEND می‌فرستد؛ پس گیرنده باید
        // همان مجوز را الزام کند تا پیامک به آن تحویل شود.
        ContextCompat.registerReceiver(
            this,
            receiver,
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
            SmsRetriever.SEND_PERMISSION,
            null,
            ContextCompat.RECEIVER_EXPORTED,
        )
    }

    fun stopOtpAutofill() {
        otpSmsReceiver?.let { runCatching { unregisterReceiver(it) } }
        otpSmsReceiver = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopOtpAutofill()
    }
}
