package com.approagency.pharmacy.domain.model

/** محصول اشتراک قابل‌نمایش در صفحه‌ی خرید. */
data class SubscriptionProduct(
    val id: Int,
    val title: String,
    val price: Long,
    val uuid: String?,
    val description: String?
)
