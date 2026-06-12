package com.approagency.pharmacy.presentation.viewModel

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.approagency.pharmacy.data.local.AccountState
import com.approagency.pharmacy.data.local.SessionManager
import com.approagency.pharmacy.domain.billing.PurchaseGateway
import com.approagency.pharmacy.domain.model.SubscriptionProduct
import com.approagency.pharmacy.domain.repository.AuthRepository
import com.approagency.pharmacy.utils.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * مغزِ جریانِ حساب: ورود با کد یک‌بارمصرف، نمایش/خرید محصولات و به‌روزرسانی وضعیت.
 *
 * منبعِ حقیقتِ وضعیت حساب [SessionManager.account] است؛ این ViewModel فقط آن را
 * می‌خواند و با فراخوانی سرور به‌روزرسانی می‌کند تا کل اپ واکنشی بماند.
 */
class AccountViewModel(
    private val repository: AuthRepository,
    private val gateway: PurchaseGateway,
    private val session: SessionManager
) : ViewModel() {

    val account: StateFlow<AccountState> = session.account

    val gatewayAvailable: Boolean get() = gateway.isAvailable

    var mobile by mutableStateOf(session.account.value.mobile.orEmpty())
        private set
    var otp by mutableStateOf("")
        private set

    private val _ui = MutableStateFlow(AccountUiState())
    val ui: StateFlow<AccountUiState> = _ui.asStateFlow()

    fun updateMobile(value: String) {
        mobile = value.filter { it.isDigit() }.take(11)
    }

    fun updateOtp(value: String) {
        otp = value.filter { it.isDigit() }.take(6)
    }

    /** هنگام باز شدن شیت: فاز مناسب را بر اساس وضعیت حساب تعیین کن. */
    fun onSheetOpened() {
        val state = account.value
        when {
            !state.isLoggedIn -> _ui.update { it.copy(phase = AccountPhase.EnterMobile, error = null) }
            !state.isSubscribed -> {
                _ui.update { it.copy(phase = AccountPhase.Products, error = null) }
                loadProducts()
            }
            else -> _ui.update { it.copy(phase = AccountPhase.Subscribed, error = null) }
        }
    }

    fun sendOtp() {
        val normalized = mobile.trim()
        if (normalized.length != 11 || !normalized.startsWith("09")) {
            _ui.update { it.copy(error = "شماره موبایل معتبر نیست.") }
            return
        }
        _ui.update { it.copy(busy = true, error = null) }
        viewModelScope.launch {
            repository.loginOtp(normalized).fold(
                onSuccess = { _ui.update { it.copy(busy = false, phase = AccountPhase.EnterOtp) } },
                onFailure = { e -> _ui.update { it.copy(busy = false, error = e.toUserMessage()) } }
            )
        }
    }

    fun verifyOtp() {
        val code = otp.trim()
        if (code.isBlank()) {
            _ui.update { it.copy(error = "کد را وارد کنید.") }
            return
        }
        _ui.update { it.copy(busy = true, error = null) }
        viewModelScope.launch {
            repository.checkOtp(mobile.trim(), code).fold(
                onSuccess = {
                    otp = ""
                    // پس از ورود، وضعیت حساب از سرور آمده است؛ فاز مناسب را تعیین کن.
                    if (account.value.isSubscribed) {
                        _ui.update { it.copy(busy = false, phase = AccountPhase.Subscribed) }
                    } else {
                        _ui.update { it.copy(busy = false, phase = AccountPhase.Products) }
                        loadProducts()
                    }
                },
                onFailure = { e -> _ui.update { it.copy(busy = false, error = e.toUserMessage()) } }
            )
        }
    }

    fun editMobile() {
        otp = ""
        _ui.update { it.copy(phase = AccountPhase.EnterMobile, error = null) }
    }

    fun loadProducts() {
        _ui.update { it.copy(productsLoading = true, error = null) }
        viewModelScope.launch {
            repository.getProducts().fold(
                onSuccess = { list -> _ui.update { it.copy(productsLoading = false, products = list) } },
                onFailure = { e -> _ui.update { it.copy(productsLoading = false, error = e.toUserMessage()) } }
            )
        }
    }

    /** خرید [product]: ابتدا درگاه فروشگاه، سپس ثبت روی سرور آپرواجنسی. */
    fun purchase(activity: Activity, product: SubscriptionProduct) {
        if (!gateway.isAvailable) {
            _ui.update { it.copy(error = "درگاه پرداخت روی این نسخه فعال نیست.") }
            return
        }
        _ui.update { it.copy(purchasingProductId = product.id, error = null) }
        viewModelScope.launch {
            val purchase = gateway.purchase(activity, product)
            val result = purchase.getOrNull()
            if (result == null) {
                _ui.update {
                    it.copy(purchasingProductId = null, error = purchase.exceptionOrNull().toUserMessage())
                }
                return@launch
            }
            repository.subscribe(product.id, result.purchaseToken, gateway.name).fold(
                onSuccess = {
                    _ui.update {
                        it.copy(
                            purchasingProductId = null,
                            phase = if (account.value.isSubscribed) AccountPhase.Subscribed else it.phase,
                            purchaseSuccess = true
                        )
                    }
                },
                onFailure = { e ->
                    _ui.update { it.copy(purchasingProductId = null, error = e.toUserMessage()) }
                }
            )
        }
    }

    /** به‌روزرسانی وضعیت اشتراک از سرور (شروع اپ / بازگشت از درگاه). */
    fun refreshStatus() {
        if (!session.isLoggedIn) return
        viewModelScope.launch { repository.refreshStatus() }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _ui.update { it.copy(phase = AccountPhase.EnterMobile, products = emptyList()) }
        }
    }

    fun consumePurchaseSuccess() {
        _ui.update { it.copy(purchaseSuccess = false) }
    }

    fun dismissError() {
        _ui.update { it.copy(error = null) }
    }
}

enum class AccountPhase { EnterMobile, EnterOtp, Products, Subscribed }

data class AccountUiState(
    val phase: AccountPhase = AccountPhase.EnterMobile,
    val busy: Boolean = false,
    val error: String? = null,
    val productsLoading: Boolean = false,
    val products: List<SubscriptionProduct> = emptyList(),
    val purchasingProductId: Int? = null,
    val purchaseSuccess: Boolean = false
)
