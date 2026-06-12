package com.approagency.pharmacy.data.remote

import com.approagency.pharmacy.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * هدرهای لازم برای بک‌اند آپرواجنسی را اضافه می‌کند:
 *  - `Accept: application/json`
 *  - `Authorization: Bearer <token>` در صورت وجود توکن
 *
 * این اینترسپتور فقط روی کلاینت اختصاصیِ آپرواجنسی نصب می‌شود؛ بنابراین توکن
 * هرگز به دارویاب یا بک‌اند جستجوی دارو ارسال نمی‌شود.
 */
class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
            .header("Accept", "application/json")

        sessionManager.getToken()?.takeIf { it.isNotBlank() }?.let { token ->
            builder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(builder.build())
    }
}
