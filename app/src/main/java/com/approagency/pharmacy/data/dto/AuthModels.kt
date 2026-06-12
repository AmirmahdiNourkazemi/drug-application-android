package com.approagency.pharmacy.data.dto

import com.google.gson.annotations.SerializedName

/** پاسخ ارسال کد یک‌بارمصرف (login-otp). */
data class LoginOtpResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("status") val status: String? = null
)

/** پاسخ تأیید کد (check-otp) که توکن نشست را برمی‌گرداند. */
data class CheckOtpResponse(
    @SerializedName("token") val token: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("user") val user: UserDto? = null
)

data class UserDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("mobile") val mobile: String? = null,
    @SerializedName("email") val email: String? = null
)

/** یک محصول اشتراک از `package-names/{name}/products`. */
data class ProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName("package_name_id") val packageNameId: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("price") val price: Long? = null,
    @SerializedName("type") val type: Int? = null,
    @SerializedName("uuid") val uuid: String? = null,
    @SerializedName("descriptions") val descriptions: String? = null,
    @SerializedName("expires_at") val expiresAt: String? = null,
    @SerializedName("expire_at") val expireAt: String? = null,
    @SerializedName("pivot") val pivot: ProductPivot? = null
) {
    /** تاریخ انقضای اشتراکِ این کاربر؛ از pivot یا فیلدهای مستقیمِ محصول. */
    val resolvedExpireAt: String?
        get() = pivot?.expiresAt ?: pivot?.expireAt ?: expiresAt ?: expireAt
}

/** اطلاعات رابطه‌ی کاربر-محصول (شامل تاریخ انقضای خرید). */
data class ProductPivot(
    @SerializedName("expires_at") val expiresAt: String? = null,
    @SerializedName("expire_at") val expireAt: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

/**
 * پاسخ `GET /status` که همان پروفایل کاربر به‌همراه فهرست محصولاتِ خریداری‌شده است.
 * اشتراک فعال یعنی [products] خالی نباشد؛ عنوان اشتراک از اولین محصول خوانده می‌شود.
 */
data class StatusDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("mobile") val mobile: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("avatar") val avatar: String? = null,
    @SerializedName("wallet") val wallet: Long? = null,
    @SerializedName("products") val products: List<ProductDto>? = null
) {
    /** کاربر در صورت داشتن حداقل یک محصول، اشتراک فعال دارد. */
    val isSubscribed: Boolean get() = !products.isNullOrEmpty()

    /** عنوان اشتراک جاری برای نمایش در نوار بالای اپ. */
    val subscriptionTitle: String? get() = products?.firstOrNull()?.title

    /** تاریخ انقضای اشتراک جاری. */
    val subscriptionExpireAt: String? get() = products?.firstOrNull()?.resolvedExpireAt

    val displayName: String? get() = fullName?.takeIf { it.isNotBlank() }
        ?: listOfNotNull(firstName, lastName).joinToString(" ").takeIf { it.isNotBlank() }
}

/** بدنه‌ی درخواست خرید اشتراک. */
data class SubscribeRequest(
    @SerializedName("purchase_token") val purchaseToken: String,
    @SerializedName("gateway") val gateway: String
)

/** پاسخ خرید اشتراک. موفقیت اصلی با کد ۲xx مشخص می‌شود. */
data class SubscribeResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("status") val status: String? = null
)
