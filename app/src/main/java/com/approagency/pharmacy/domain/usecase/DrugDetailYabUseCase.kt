package com.approagency.pharmacy.domain.usecase

import com.approagency.pharmacy.domain.model.DrugDetail
import com.approagency.pharmacy.domain.repository.DrugRepository

class DrugDetailYabUseCase (
    private  val repository: DrugRepository
) {
    suspend operator fun invoke(detailUrl: String):Result<DrugDetail> {
        return repository.getDrugDetailFromYab(detailUrl)
    }
}