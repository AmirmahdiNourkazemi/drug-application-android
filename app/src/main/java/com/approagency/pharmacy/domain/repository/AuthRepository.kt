package com.approagency.pharmacy.domain.repository

import com.approagency.pharmacy.domain.model.SubscriptionProduct

interface AuthRepository {

    /** ارسال کد یک‌بارمصرف به [mobile]. */
    suspend fun loginOtp(mobile: String): Result<Unit>

    /** تأیید [code] برای [mobile]؛ در صورت موفقیت توکن ذخیره می‌شود. */
    suspend fun checkOtp(mobile: String, code: String): Result<Unit>

    /** خروج از حساب و پاک‌سازی نشست. */
    suspend fun logout(): Result<Unit>

    /**
     * خواندن `/status` و به‌روزرسانی [SessionManager.account].
     * مقدار بازگشتی نشان می‌دهد کاربر اشتراک فعال دارد یا نه.
     */
    suspend fun refreshStatus(): Result<Boolean>

    /** فهرست محصولات اشتراک قابل خرید. */
    suspend fun getProducts(): Result<List<SubscriptionProduct>>

    /** ثبت خرید اشتراک پس از پرداخت موفق در درگاه. */
    suspend fun subscribe(productId: Int, purchaseToken: String, gateway: String): Result<Unit>
}
