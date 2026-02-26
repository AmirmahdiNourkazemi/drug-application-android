package com.approagency.drug.data.repository

import com.approagency.drug.data.dto.DrugListResponse
import com.approagency.drug.data.remote.DrugApiService
import com.approagency.drug.domain.model.DrugSearchParams
import com.approagency.drug.domain.repository.DrugRepository

class DrugRepositoryImpl(
    private val apiService: DrugApiService
): DrugRepository {
    override suspend fun searchDrug(params: DrugSearchParams): Result<DrugListResponse> {
        return try {
            val response = apiService.getDrugs(query = params.query , perPage = params.perPage , withRelations = params.withRelations , healGroup = params.healGroup , drugGroup = params.drugGroup )
            return Result.success(response)
        }catch (e: Exception){
            Result.failure(e)
        }
    }
}