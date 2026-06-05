package com.approagency.pharmacy.domain.usecase

import com.approagency.pharmacy.data.dto.DarmanModel
import com.approagency.pharmacy.domain.repository.DrugRepository

class GetDarmanUseCase (
  private val repository: DrugRepository
){
    suspend operator fun invoke (): Result<DarmanModel>{
        return repository.getGorohDaroei()
    }
}