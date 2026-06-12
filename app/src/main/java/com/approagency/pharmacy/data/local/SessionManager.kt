package com.approagency.pharmacy.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** وضعیت حساب کاربر که در کل اپ مشاهده می‌شود. */
data class AccountState(
    val isLoggedIn: Boolean = false,
    val mobile: String? = null,
    val displayName: String? = null,
    val isSubscribed: Boolean = false,
    val subscriptionTitle: String? = null
)

/**
 * تنها منبعِ حقیقتِ نشست کاربر روی دستگاه (SharedPreferences).
 *
 * مسئولیت‌ها:
 *  - توکن احراز هویت آپرواجنسی
 *  - شمارنده‌ی جستجوهای رایگان دارویاب
 *  - کشِ پایدارِ وضعیت حساب/اشتراک ([AccountState]) که در کل اپ به‌صورت واکنشی
 *    مشاهده می‌شود و هنگام شروع اپ یا بازگشت از درگاه پرداخت به‌روزرسانی می‌گردد
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ---------- توکن احراز هویت ----------

    private var token: String? = prefs.getString(KEY_TOKEN, null)

    val isLoggedIn: Boolean get() = !token.isNullOrBlank()

    fun saveToken(token: String) {
        this.token = token
        prefs.edit().putString(KEY_TOKEN, token).apply()
        publishAccount()
    }

    fun getToken(): String? = token

    // ---------- شمارنده‌ی جستجوی رایگان ----------

    private val _freeSearchCount = MutableStateFlow(prefs.getInt(KEY_FREE_SEARCH_COUNT, 0))
    val freeSearchCount: StateFlow<Int> = _freeSearchCount.asStateFlow()

    fun incrementFreeSearchCount() {
        val next = _freeSearchCount.value + 1
        prefs.edit().putInt(KEY_FREE_SEARCH_COUNT, next).apply()
        _freeSearchCount.value = next
    }

    fun resetFreeSearchCount() {
        prefs.edit().putInt(KEY_FREE_SEARCH_COUNT, 0).apply()
        _freeSearchCount.value = 0
    }

    fun testForIncrease (){
        prefs.edit().putInt(KEY_FREE_SEARCH_COUNT, -1).apply()
        _freeSearchCount.value = -1
    }
    // ---------- وضعیت حساب/اشتراک (پایدار و واکنشی) ----------

    private val _account = MutableStateFlow(readAccount())
    val account: StateFlow<AccountState> = _account.asStateFlow()

    val isSubscribed: Boolean get() = _account.value.isSubscribed

    /** به‌روزرسانی وضعیت حساب از روی پاسخ `/status` و ذخیره‌ی پایدار آن. */
    fun updateAccount(
        mobile: String?,
        displayName: String?,
        isSubscribed: Boolean,
        subscriptionTitle: String?
    ) {
        prefs.edit()
            .putString(KEY_MOBILE, mobile)
            .putString(KEY_DISPLAY_NAME, displayName)
            .putBoolean(KEY_IS_SUBSCRIBED, isSubscribed)
            .putString(KEY_SUB_TITLE, subscriptionTitle)
            .apply()
        publishAccount()
    }

    /** ذخیره‌ی شماره‌ی موبایل واردشده (پیش از تکمیل ورود). */
    fun saveMobile(mobile: String) {
        prefs.edit().putString(KEY_MOBILE, mobile).apply()
        publishAccount()
    }

    /** پاک‌سازی کامل نشست هنگام خروج از حساب. */
    fun clear() {
        token = null
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_MOBILE)
            .remove(KEY_DISPLAY_NAME)
            .remove(KEY_IS_SUBSCRIBED)
            .remove(KEY_SUB_TITLE)
            .apply()
        publishAccount()
    }

    private fun publishAccount() {
        _account.value = readAccount()
    }

    private fun readAccount() = AccountState(
        isLoggedIn = !token.isNullOrBlank(),
        mobile = prefs.getString(KEY_MOBILE, null),
        displayName = prefs.getString(KEY_DISPLAY_NAME, null),
        isSubscribed = prefs.getBoolean(KEY_IS_SUBSCRIBED, false),
        subscriptionTitle = prefs.getString(KEY_SUB_TITLE, null)
    )

    private companion object {
        const val PREFS_NAME = "approagency_session"
        const val KEY_TOKEN = "auth_token"
        const val KEY_MOBILE = "mobile"
        const val KEY_DISPLAY_NAME = "display_name"
        const val KEY_FREE_SEARCH_COUNT = "free_search_count"
        const val KEY_IS_SUBSCRIBED = "is_subscribed"
        const val KEY_SUB_TITLE = "subscription_title"
    }
}
