package com.approagency.drug.domain.usecase

import com.approagency.drug.data.repository.DrugRepositoryImpl
import com.approagency.drug.domain.model.DaroYabParams
import com.approagency.drug.domain.model.DaroYabSearchResult
import com.approagency.drug.domain.model.DrugSearchParams
import com.approagency.drug.domain.model.DrugSearchResult
import com.approagency.drug.domain.repository.DrugRepository

class SearchDrugsYabUseCase (
    private  val repository: DrugRepository
) {
    suspend operator fun invoke(query: String, pageNumber: Int = 1): Result<DaroYabSearchResult> {
        val params = DaroYabParams(query = query, pageNumber = pageNumber)
        return repository.searchDrugs(params)
    }
}
