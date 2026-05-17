package com.approagency.drug.domain.usecase

import com.approagency.drug.data.repository.DrugRepositoryImpl
import com.approagency.drug.domain.model.DrugSearchParams
import com.approagency.drug.domain.model.DrugSearchResult
import com.approagency.drug.domain.repository.DrugRepository

class SearchDrugsYabUseCase (
    private  val repository: DrugRepository
) {
    suspend operator fun invoke(query: String): Result<List<DrugSearchResult>> {
        val params = DrugSearchParams(query = query)
        return repository.searchDrugs(params)
    }
}