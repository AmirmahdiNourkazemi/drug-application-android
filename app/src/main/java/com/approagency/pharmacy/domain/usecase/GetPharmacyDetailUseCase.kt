package com.approagency.pharmacy.domain.usecase

import com.approagency.pharmacy.domain.model.PharmacyDetail
import com.approagency.pharmacy.domain.repository.DrugRepository

class GetPharmacyDetailUseCase(
    private val repository: DrugRepository
) {
    suspend operator fun invoke(pharmacyUrl: String): PharmacyDetail {
        return repository.getPharmacyDetail(pharmacyUrl)
    }
}