package com.approagency.pharmacy.utils

object Config {
    const val BASE_URL = "https://drug.approagency.ir/api/"
    const val Darro_Url = "https://www.darooyab.ir/"

    /** بک‌اند اشتراک/احراز هویت آپرواجنسی (لاگین، وضعیت، محصولات، خرید). */
    const val AUTH_BASE_URL = "https://api.approagency.ir/api/"

    /** نام پکیج این اپ در سامانه‌ی آپرواجنسی (برای status / products / subscribe). */
    const val PACKAGE_NAME = "com.approagency.pharmacy"

    /** تعداد جستجوی رایگان دارویاب پیش از نیاز به ورود و اشتراک. */
    const val FREE_SEARCH_LIMIT = 2
}