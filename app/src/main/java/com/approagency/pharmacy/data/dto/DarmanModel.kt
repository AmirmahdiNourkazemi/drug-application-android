package com.approagency.pharmacy.data.dto

data class DarmanModel(
    val success : String,
    val message:String,
    val data: List<DarmanList>
)

data class DarmanList(
    val cod : Int,
    val nam_fa:String,
    val nam_en:String,
)