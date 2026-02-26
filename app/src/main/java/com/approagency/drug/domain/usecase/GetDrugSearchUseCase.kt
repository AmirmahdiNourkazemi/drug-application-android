package com.approagency.drug.domain.usecase

import com.approagency.drug.data.dto.DrugListResponse
import com.approagency.drug.domain.model.DrugSearchParams
import com.approagency.drug.domain.repository.DrugRepository

class GetDrugSearchUseCase (
    private val repository: DrugRepository
){
    suspend operator fun invoke(params: DrugSearchParams): Result<DrugListResponse> {
        return repository.searchDrug(params)
    }
}