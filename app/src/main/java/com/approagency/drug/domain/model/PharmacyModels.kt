package com.approagency.drug.domain.model

data class PharmacySearchRequest(
    val brandIrc: String = "0",     // برای صفحات برند
    val genericDrugId: String,       // برای صفحات ژنریک
    val provId: String,              // ID استان (0 برای همه)
    val cityId: String = "0"         // ID شهر (0 برای همه)
)

data class PharmacyItem(
    val brandName: String,           // نام برند دارو
    val brandUrl: String,            // لینک صفحه برند
    val brandId: String,             // IRC برند

    val pharmacyName: String,        // نام داروخانه
    val pharmacyUrl: String,         // لینک داروخانه
    val pharmacyId: String,          // ID داروخانه

    val province: String,            // نام استان
    val city: String    ,             // نام شهر
    val address: String? = null,
    val phone: String? = null
)

// مدل برای اطلاعات کامل داروخانه
data class PharmacyDetail(
    val name: String,
    val address: String,
    val phone: String,
    val university: String? = null
)