package com.approagency.drug.data.dto

data class DrugModels(
    val success: Boolean,
    val message: String?,
    val data: DrugDetail?,
    val meta: Meta?
)

data class DrugDetail(
    val cod: Int?,
    val goroh_darmani_detail_cod: Int?,
    val goroh_daroei_cod: Int?,
    val goroh_farmakologic_cod: Int?,
    val goroh_darmani_cod: Int?,
    val nam_fa: String?,
    val nam_en: String?,
    val mavaredmasraf: String?,
    val meghdarmasraf: String?,
    val masrafdarhamelegi: String?,
    val masrafdarshirdehi: String?,
    val manemasraf: String?,
    val avarez: String?,
    val tadakhol: String?,
    val mekanismtasir: String?,
    val nokte: String?,
    val hoshdar: String?,
    val sharayetnegahdari: String?,
    val ashkal_daroei: String?,
    val created_at: String?,
    val updated_at: String?,
    val goroh_daroei: GorohDaroei?,
    val goroh_darmani: GorohDarmani?,
    val goroh_darmani_detail: GorohDarmaniDetail?
)

data class GorohDaroei(
    val cod: Int?,
    val nam: String?,
    val created_at: String?,
    val updated_at: String?
)

data class GorohDarmani(
    val cod: Int?,
    val nam_fa: String?,
    val nam_en: String?,
    val giyahi_ya_shimiyaei: String?,
    val image: String?,
    val created_at: String?,
    val updated_at: String?
)

data class GorohDarmaniDetail(
    val cod: Int?,
    val nam: String?,
    val created_at: String?,
    val updated_at: String?
)

data class Meta(
    val current_page: Int?,
    val last_page: Int?,
    val per_page: Int?,
    val total: Int?
)

// For list response compatibility
data class DrugListResponse(
    val success: Boolean,
    val message: String?,
    val data: List<DrugDetail> = emptyList(),
    val meta: Meta?
)