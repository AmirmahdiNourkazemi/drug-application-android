package com.approagency.pharmacy.data.repository

import com.approagency.pharmacy.data.dto.DarmanModel
import com.approagency.pharmacy.data.dto.DrugListResponse
import com.approagency.pharmacy.data.dto.DrugModels
import com.approagency.pharmacy.data.remote.DarooyabApiService
import com.approagency.pharmacy.data.remote.DrugApiService
import com.approagency.pharmacy.data.remote.DrugDetailParser
import com.approagency.pharmacy.data.remote.DrugHtmlParser
import com.approagency.pharmacy.data.remote.PharmacyHtmlParser
import com.approagency.pharmacy.domain.model.DaroYabParams
import com.approagency.pharmacy.domain.model.DaroYabSearchResult
import com.approagency.pharmacy.domain.model.DrugDetail
import com.approagency.pharmacy.domain.model.DrugSearchParams
import com.approagency.pharmacy.domain.model.PharmacyDetail
import com.approagency.pharmacy.domain.model.PharmacyItem
import com.approagency.pharmacy.domain.repository.DrugRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class DrugRepositoryImpl(
    private val apiService: DrugApiService,
    private val darooyabApiService: DarooyabApiService,
    private val parser: DrugHtmlParser,
    private val detailParser: DrugDetailParser,
): DrugRepository {
    override suspend fun searchDrug(params: DrugSearchParams): Result<DrugListResponse> {
        return try {
            val response = apiService.getDrugs(query = params.query , perPage = params.perPage , withRelations = params.withRelations , healGroup = params.healGroup , drugGroup = params.drugGroup )
            return Result.success(response)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun drugDetail(cod: Int): Result<DrugModels> {
        return try {
            val response= apiService.getDrugDetail(cod = cod)
            return Result.success(response)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getGorohDaroei(): Result<DarmanModel> {
        return  try {
             val response = apiService.getGorohDaroei()
            return Result.success(response)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun searchDrugs(params: DaroYabParams): Result<DaroYabSearchResult> {
        return withContext(Dispatchers.IO) {
            try {
                val htmlResponse = darooyabApiService.searchDrugs(
                    searchText = params.query ?: "",
                    pageNumber = params.pageNumber
                )

                val searchResult = parser.parseSearchResultsWithPagination(htmlResponse)

                Result.success(searchResult)
            } catch (e: IOException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getDrugDetailFromYab(detailUrl: String): Result<DrugDetail> {
        return withContext(Dispatchers.IO) {
            try {
                val html = darooyabApiService.getDrugDetail(detailUrl)
                val detail = detailParser.parseDrugDetail(html)
                Result.success(detail)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun searchPharmacies(
        genericDrugId: String,
        brandIrc: String,
        provId: String
    ): List<PharmacyItem> {
        return withContext(Dispatchers.IO) {
            try {
                val html = darooyabApiService.getPharmacies(
                    brandIrc = brandIrc,
                    genericDrugId = genericDrugId,
                    provId = provId,
                    cityId = "0"
                )
                PharmacyHtmlParser.parse(html)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override suspend fun getPharmacyDetail(pharmacyUrl: String): PharmacyDetail {
        return withContext(Dispatchers.IO) {
            try {
                val html = darooyabApiService.getPharmacyDetail(pharmacyUrl)
                PharmacyHtmlParser.parsePharmacyDetail(html)
            } catch (e: Exception) {
                PharmacyDetail("", "", "")
            }
        }
    }
}