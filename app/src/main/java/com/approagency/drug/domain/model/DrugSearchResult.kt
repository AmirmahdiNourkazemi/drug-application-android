package com.approagency.drug.domain.model

data class DrugSearchResult(
    val genericId: String, // e.g., "2556" from "/G-2556/Casanthranol"
    val persianName: String,
    val englishName: String? = null, // "Casanthranol", might be null for some entries
    val detailPageUrl: String // e.g., "/G-2556/Casanthranol" or "https://www.darooyab.ir/G-2556/Casanthranol"
)