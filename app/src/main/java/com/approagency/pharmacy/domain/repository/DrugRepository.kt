package com.approagency.pharmacy.domain.repository

import com.approagency.pharmacy.data.dto.DarmanModel
import com.approagency.pharmacy.data.dto.DrugListResponse
import com.approagency.pharmacy.data.dto.DrugModels
import com.approagency.pharmacy.domain.model.DaroYabParams
import com.approagency.pharmacy.domain.model.DaroYabSearchResult
import com.approagency.pharmacy.domain.model.DrugDetail
import com.approagency.pharmacy.domain.model.DrugSearchParams
import com.approagency.pharmacy.domain.model.PharmacyDetail
import com.approagency.pharmacy.domain.model.PharmacyItem

interface DrugRepository {
    suspend fun searchDrug(params: DrugSearchParams): Result<DrugListResponse>
    suspend fun drugDetail(cod:Int): Result<DrugModels>
    suspend fun getGorohDaroei(): Result<DarmanModel>
    suspend fun searchDrugs(params:DaroYabParams):Result<DaroYabSearchResult>
    suspend fun getDrugDetailFromYab(detailUrl: String):Result<DrugDetail>
    suspend fun searchPharmacies(genericDrugId: String, brandIrc: String, provId: String): List<PharmacyItem>

    suspend fun getPharmacyDetail(pharmacyUrl: String): PharmacyDetail

}