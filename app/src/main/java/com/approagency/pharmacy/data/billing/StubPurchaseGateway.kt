package com.approagency.pharmacy.data.billing

import android.app.Activity
import com.approagency.pharmacy.domain.billing.PurchaseGateway
import com.approagency.pharmacy.domain.billing.PurchaseResult
import com.approagency.pharmacy.domain.model.SubscriptionProduct

/**
 * پیاده‌سازی پیش‌فرض درگاه پرداخت روی شاخه‌ی `main`.
 *
 * هیچ درگاه واقعی‌ای ندارد؛ شاخه‌های `myket` و `bazar` این را با پیاده‌سازی
 * فروشگاه خود جایگزین می‌کنند. تا آن زمان، خرید با پیام مناسب ناموفق می‌شود.
 */
class StubPurchaseGateway : PurchaseGateway {

    override val name: String = "none"

    override val isAvailable: Boolean = false

    override suspend fun purchase(
        activity: Activity,
        product: SubscriptionProduct
    ): Result<PurchaseResult> =
        Result.failure(IllegalStateException("درگاه پرداخت روی این نسخه فعال نیست."))
}
