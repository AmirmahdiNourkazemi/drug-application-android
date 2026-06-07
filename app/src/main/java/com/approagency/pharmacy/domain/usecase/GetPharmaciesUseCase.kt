package com.approagency.pharmacy.domain.usecase

import com.approagency.pharmacy.domain.model.PharmacyItem
import com.approagency.pharmacy.domain.repository.DrugRepository

class GetPharmaciesUseCase(
    private val repository: DrugRepository
) {
    suspend operator fun invoke(
        genericDrugId: String,
        brandIrc: String,
        provinceId: String
    ): List<PharmacyItem> {
        return repository.searchPharmacies(genericDrugId, brandIrc, provinceId)
    }
}