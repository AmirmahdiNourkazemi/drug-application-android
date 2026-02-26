package com.approagency.drug.domain.model

data class DrugSearchParams(
    val query: String? = null,
    val perPage: Int = 20,
    val withRelations: Boolean = true,
    val drugGroup: Int? = null,
    val healGroup: Int? = null,
)
