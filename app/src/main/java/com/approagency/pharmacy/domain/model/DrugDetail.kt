package com.approagency.pharmacy.domain.model

data class DrugDetail(
    val genericId: String,
    val persianName: String,
    val englishName: String,
    val drugClass: String?,
    val therapeuticClass: String?,
    val usage: String?,
    val mechanism: String?,
    val pharmacokinetics: String?,
    val contraindications: String?,
    val sideEffects: String?,
    val interactions: String?,
    val warnings: String?,
    val recommendations: String?,
    val pregnancyCategory: String?,
    val pregnancyDescription: String?,
    val dosageForms: List<DosageForm>,
    val brandNames: List<BrandName>,
    val similarDrugs: List<SimilarDrug>,
    val categories: DrugCategories?,
    val comments: List<Comment>,
    val manufacturer: String? = null,
    val genericInfo: GenericInfo? = null,
    val otherBrandForms: List<DosageForm> = emptyList(),
    val isGeneric: Boolean = true,
    val generalInfo: String? = null,
    val specializedInfo: String? = null,
)
data class GenericInfo(
    val genericId: String,
    val persianName: String,
    val detailUrl: String
)
data class DosageForm(
    val code: String,
    val persianName: String,
    val englishName: String,
    val isHighRisk: Boolean,
    val temperature: String?,
    val isVital: Boolean,
    val warningLabel: String?,
    val detailUrl: String? = null
)

data class BrandName(
    val persianName: String,
    val englishName: String,
    val manufacturer: String?,
    val importer: String?,
    val detailUrl: String
)

data class SimilarDrug(
    val persianName: String,
    val englishName: String?,
    val genericId: String,
    val detailUrl: String
)

data class DrugCategories(
    val martindale: String?,
    val martindaleUrl: String?,
    val therapeutic: List<String>?,
    val therapeuticUrls: List<String>?
)

data class Comment(
    val author: String,
    val date: String,
    val text: String,
    val response: CommentResponse?
)

data class CommentResponse(
    val doctorName: String,
    val doctorTitle: String,
    val text: String,
    val doctorUrl: String?
)