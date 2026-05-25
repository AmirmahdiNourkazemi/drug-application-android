package com.approagency.drug.domain.model

data class DaroYabSearchResult(
    val drugs: List<DrugSearchResult>,
    val currentPage: Int,
    val totalPages: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean
)