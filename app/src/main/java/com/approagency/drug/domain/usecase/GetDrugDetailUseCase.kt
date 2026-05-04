package com.approagency.drug.domain.usecase

import com.approagency.drug.data.dto.DrugModels
import com.approagency.drug.domain.repository.DrugRepository

class GetDrugDetailUseCase
    (
    private val repository: DrugRepository){
    suspend operator fun invoke(cod:Int): Result<DrugModels>{
        return repository.drugDetail(cod)
    }
}