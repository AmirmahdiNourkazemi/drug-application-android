package com.approagency.drug.domain.repository

import com.approagency.drug.data.dto.DarmanModel
import com.approagency.drug.data.dto.DrugListResponse
import com.approagency.drug.data.dto.DrugModels
import com.approagency.drug.domain.model.DrugSearchParams

interface DrugRepository {
    suspend fun searchDrug(params: DrugSearchParams): Result<DrugListResponse>
    suspend fun drugDetail(cod:Int): Result<DrugModels>
    suspend fun getGorohDaroei(): Result<DarmanModel>
}