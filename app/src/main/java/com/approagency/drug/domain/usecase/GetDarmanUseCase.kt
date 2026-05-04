package com.approagency.drug.domain.usecase

import com.approagency.drug.data.dto.DarmanModel
import com.approagency.drug.domain.repository.DrugRepository

class GetDarmanUseCase (
  private val repository: DrugRepository
){
    suspend operator fun invoke (): Result<DarmanModel>{
        return repository.getGorohDaroei()
    }
}