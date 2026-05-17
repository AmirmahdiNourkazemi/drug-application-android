package com.approagency.drug.data.repository

import com.approagency.drug.data.dto.DarmanModel
import com.approagency.drug.data.dto.DrugListResponse
import com.approagency.drug.data.dto.DrugModels
import com.approagency.drug.data.remote.DarooyabApiService
import com.approagency.drug.data.remote.DrugApiService
import com.approagency.drug.data.remote.DrugHtmlParser
import com.approagency.drug.domain.model.DrugSearchParams
import com.approagency.drug.domain.model.DrugSearchResult
import com.approagency.drug.domain.repository.DrugRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class DrugRepositoryImpl(
    private val apiService: DrugApiService,
    private val darooyabApiService: DarooyabApiService,
    private val parser: DrugHtmlParser
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

    override suspend fun searchDrugs(params: DrugSearchParams): Result<List<DrugSearchResult>> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Fetch raw HTML from the website
                val htmlResponse = darooyabApiService.searchDrugs(params.query!!)

                // 2. Parse the HTML to extract drug data
                val drugList = parser.parseSearchResults(htmlResponse)

                if (drugList.isNotEmpty()) {
                    Result.success(drugList)
                } else {
                    Result.failure(Exception("No drugs found for query: '${params.query}'"))
                }
            } catch (e: IOException) {
                // Network error
                Result.failure(Exception("Network error: ${e.message}", e))
            } catch (e: Exception) {
                // Parsing or other error
                Result.failure(Exception("An error occurred: ${e.message}", e))
            }
        }
    }
}