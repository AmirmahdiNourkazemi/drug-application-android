package com.approagency.drug.data.repository

import com.approagency.drug.data.dto.DarmanModel
import com.approagency.drug.data.dto.DrugListResponse
import com.approagency.drug.data.dto.DrugModels
import com.approagency.drug.data.remote.DarooyabApiService
import com.approagency.drug.data.remote.DrugApiService
import com.approagency.drug.data.remote.DrugDetailParser
import com.approagency.drug.data.remote.DrugHtmlParser
import com.approagency.drug.data.remote.PharmacyHtmlParser
import com.approagency.drug.domain.model.DaroYabParams
import com.approagency.drug.domain.model.DaroYabSearchResult
import com.approagency.drug.domain.model.DrugDetail
import com.approagency.drug.domain.model.DrugSearchParams
import com.approagency.drug.domain.model.DrugSearchResult
import com.approagency.drug.domain.model.PharmacyItem
import com.approagency.drug.domain.repository.DrugRepository
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
            println(response.message)
            return Result.success(response)
        }catch (e: Exception){
            println(e.message)
            Result.failure(e)
        }
    }

    override suspend fun getGorohDaroei(): Result<DarmanModel> {
        return  try {
             val response = apiService.getGorohDaroei()
            println(response)
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

                println("HTML Response length: ${htmlResponse.length}")

                val searchResult = parser.parseSearchResultsWithPagination(htmlResponse)

                if (searchResult.drugs.isNotEmpty()) {
                    Result.success(searchResult)
                } else {
                    Result.failure(Exception("No drugs found for query: '${params.query}'"))
                }
            } catch (e: IOException) {
                Result.failure(Exception("Network error: ${e.message}", e))
            } catch (e: Exception) {
                Result.failure(Exception("An error occurred: ${e.message}", e))
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
        provId: String
    ): List<PharmacyItem> {
        return withContext(Dispatchers.IO) {
            try {
                val html = darooyabApiService.getPharmacies(
                    brandIrc = "0",
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
}