package com.approagency.drug.domain.usecase

import com.approagency.drug.domain.model.PharmacyItem
import com.approagency.drug.domain.repository.DrugRepository

class GetPharmaciesUseCase(
    private val repository: DrugRepository
) {
    suspend operator fun invoke(
        genericDrugId: String,
        provinceId: String
    ): List<PharmacyItem> {
        return repository.searchPharmacies(genericDrugId, provinceId)
    }
}