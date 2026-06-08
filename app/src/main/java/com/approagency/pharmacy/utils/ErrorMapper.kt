package com.approagency.pharmacy.utils

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/** پیام‌های خطای فارسیِ امن — بدون افشای منبع داده یا متن خام استثناء. */
object ErrorMessages {
    const val NETWORK = "اتصال به اینترنت برقرار نیست. لطفاً اتصال خود را بررسی کرده و دوباره تلاش کنید."
    const val TIMEOUT = "زمان پاسخ‌گویی به پایان رسید. لطفاً دوباره تلاش کنید."
    const val SERVER = "در حال حاضر امکان دریافت اطلاعات وجود ندارد. لطفاً کمی بعد دوباره تلاش کنید."
    const val NOT_FOUND = "موردی یافت نشد."
    const val GENERIC = "خطایی رخ داد. لطفاً دوباره تلاش کنید."
}

/**
 * تبدیل استثناء به پیام فارسیِ قابل‌نمایش به کاربر.
 * هرگز متن خام استثناء، آدرس سرور یا نام منبع داده را برنمی‌گرداند.
 */
fun Throwable?.toUserMessage(): String = when (this) {
    null -> ErrorMessages.GENERIC
    is UnknownHostException -> ErrorMessages.NETWORK
    is SocketTimeoutException -> ErrorMessages.TIMEOUT
    is HttpException -> ErrorMessages.SERVER
    is IOException -> ErrorMessages.NETWORK
    else -> when (cause) {
        is UnknownHostException -> ErrorMessages.NETWORK
        is SocketTimeoutException -> ErrorMessages.TIMEOUT
        is HttpException -> ErrorMessages.SERVER
        is IOException -> ErrorMessages.NETWORK
        else -> ErrorMessages.GENERIC
    }
}
