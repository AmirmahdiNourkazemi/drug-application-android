package com.approagency.drug.domain.usecase

import com.approagency.drug.domain.model.DaroYabParams
import com.approagency.drug.domain.model.DaroYabSearchResult
import com.approagency.drug.domain.model.DrugDetail
import com.approagency.drug.domain.repository.DrugRepository

class DrugDetailYabUseCase (
    private  val repository: DrugRepository
) {
    suspend operator fun invoke(detailUrl: String):Result<DrugDetail> {
        return repository.getDrugDetailFromYab(detailUrl)
    }
}