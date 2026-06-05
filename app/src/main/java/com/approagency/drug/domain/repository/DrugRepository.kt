package com.approagency.drug.domain.repository

import com.approagency.drug.data.dto.DarmanModel
import com.approagency.drug.data.dto.DrugListResponse
import com.approagency.drug.data.dto.DrugModels
import com.approagency.drug.domain.model.DaroYabParams
import com.approagency.drug.domain.model.DaroYabSearchResult
import com.approagency.drug.domain.model.DrugDetail
import com.approagency.drug.domain.model.DrugSearchParams
import com.approagency.drug.domain.model.DrugSearchResult
import com.approagency.drug.domain.model.PharmacyDetail
import com.approagency.drug.domain.model.PharmacyItem

interface DrugRepository {
    suspend fun searchDrug(params: DrugSearchParams): Result<DrugListResponse>
    suspend fun drugDetail(cod:Int): Result<DrugModels>
    suspend fun getGorohDaroei(): Result<DarmanModel>
    suspend fun searchDrugs(params:DaroYabParams):Result<DaroYabSearchResult>
    suspend fun getDrugDetailFromYab(detailUrl: String):Result<DrugDetail>
    suspend fun searchPharmacies(genericDrugId: String,provId: String): List<PharmacyItem>

    suspend fun getPharmacyDetail(pharmacyUrl: String): PharmacyDetail

}