package com.approagency.drug.domain.usecase

import com.approagency.drug.domain.model.PharmacyDetail
import com.approagency.drug.domain.repository.DrugRepository

class GetPharmacyDetailUseCase(
    private val repository: DrugRepository
) {
    suspend operator fun invoke(pharmacyUrl: String): PharmacyDetail {
        return repository.getPharmacyDetail(pharmacyUrl)
    }
}