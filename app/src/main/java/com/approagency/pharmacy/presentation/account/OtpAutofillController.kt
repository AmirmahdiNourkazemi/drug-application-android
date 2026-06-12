package com.approagency.pharmacy.presentation.account

/**
 * قراردادِ شروع/توقفِ خودکارپُرکُنِ کدِ پیامک.
 *
 * اکتیویتی میزبان آن را پیاده‌سازی می‌کند؛ شیتِ ورود فقط به این اینترفیس وابسته
 * است و از نوعِ مشخصِ اکتیویتی بی‌خبر می‌ماند.
 */
interface OtpAutofillController {
    fun startOtpAutofill()
    fun stopOtpAutofill()
}
