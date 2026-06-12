package com.approagency.pharmacy.domain.billing

import android.app.Activity
import com.approagency.pharmacy.domain.model.SubscriptionProduct

/**
 * انتزاع درگاه پرداخت درون‌برنامه‌ای.
 *
 * این اینترفیس مرز میان منطقِ مشترکِ اشتراک و پیاده‌سازیِ مخصوصِ هر فروشگاه است:
 *  - شاخه‌ی `myket`  → پیاده‌سازی مایکت (Myket IAB)
 *  - شاخه‌ی `bazar`  → پیاده‌سازی کافه‌بازار (Poolakey)
 *
 * جریان: درگاه خرید را انجام می‌دهد و یک [PurchaseResult.purchaseToken] برمی‌گرداند؛
 * سپس لایه‌ی اشتراک آن توکن را با `gateway = [name]` به سرور آپرواجنسی می‌فرستد.
 */
interface PurchaseGateway {

    /** نام درگاه که به سرور ارسال می‌شود (مثلاً "myket" یا "bazaar"). */
    val name: String

    /** آیا این درگاه روی این بیلد فعال/پیکربندی شده است؟ */
    val isAvailable: Boolean

    /**
     * خرید [product] را در فروشگاه آغاز می‌کند.
     * نیاز به [activity] برای باز کردن جریان پرداخت فروشگاه دارد.
     */
    suspend fun purchase(activity: Activity, product: SubscriptionProduct): Result<PurchaseResult>
}

/** نتیجه‌ی یک خرید موفق در فروشگاه. */
data class PurchaseResult(
    val purchaseToken: String,
    val orderId: String? = null
)
