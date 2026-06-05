package com.approagency.pharmacy.domain.usecase

import com.approagency.pharmacy.data.dto.DrugListResponse
import com.approagency.pharmacy.domain.model.DrugSearchParams
import com.approagency.pharmacy.domain.repository.DrugRepository

class GetDrugSearchUseCase (
    private val repository: DrugRepository
){
    suspend operator fun invoke(params: DrugSearchParams): Result<DrugListResponse> {
        return repository.searchDrug(params)
    }
}