package com.approagency.pharmacy.domain.usecase

import com.approagency.pharmacy.data.dto.DrugModels
import com.approagency.pharmacy.domain.repository.DrugRepository

class GetDrugDetailUseCase
    (
    private val repository: DrugRepository){
    suspend operator fun invoke(cod:Int): Result<DrugModels>{
        return repository.drugDetail(cod)
    }
}