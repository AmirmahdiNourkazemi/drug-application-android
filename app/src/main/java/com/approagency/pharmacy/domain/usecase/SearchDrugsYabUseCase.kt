package com.approagency.pharmacy.domain.usecase

import com.approagency.pharmacy.domain.model.DaroYabParams
import com.approagency.pharmacy.domain.model.DaroYabSearchResult
import com.approagency.pharmacy.domain.repository.DrugRepository

class SearchDrugsYabUseCase (
    private  val repository: DrugRepository
) {
    suspend operator fun invoke(query: String, pageNumber: Int = 1): Result<DaroYabSearchResult> {
        val params = DaroYabParams(query = query, pageNumber = pageNumber)
        return repository.searchDrugs(params)
    }
}
