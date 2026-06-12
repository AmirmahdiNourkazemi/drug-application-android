package com.approagency.pharmacy.presentation.account

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * کنترلر سراسریِ نمایش شیتِ حساب (ورود/اشتراک).
 *
 * تک‌نمونه در Koin؛ هم نوار بالای اپ و هم گیتِ جستجو می‌توانند با [show] آن را
 * باز کنند و [MainContainer] با مشاهده‌ی [visible] شیت را نمایش می‌دهد.
 */
class AccountSheetController {
    private val _visible = MutableStateFlow(false)
    val visible: StateFlow<Boolean> = _visible.asStateFlow()

    fun show() { _visible.value = true }
    fun hide() { _visible.value = false }
}
