package com.approagency.pharmacy.presentation.account

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * پلِ تک‌نمونه برای رساندن کدِ خوانده‌شده از پیامک (SMS User Consent) از
 * [MainActivity] به شیتِ ورود. اکتیویتی کد را [submit] می‌کند و شیت آن را
 * از [codes] می‌خواند و در فیلد OTP قرار می‌دهد.
 */
class OtpAutoFillBus {
    private val _codes = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val codes: SharedFlow<String> = _codes.asSharedFlow()

    fun submit(code: String) {
        _codes.tryEmit(code)
    }
}
