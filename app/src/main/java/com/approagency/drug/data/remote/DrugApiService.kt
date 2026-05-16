package com.approagency.drug.data.remote

import com.approagency.drug.data.dto.DarmanModel
import com.approagency.drug.data.dto.DrugListResponse
import com.approagency.drug.data.dto.DrugModels
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface DrugApiService {
    @GET("drugs/search")
    suspend fun getDrugs(
        @Query("q")query: String?,
        @Query("per_page")perPage:Int? = 20,
        @Query("with_relations")withRelations: Boolean? =true,
        @Query("goroh_daroei_cod")drugGroup:Int?,
        @Query("goroh_darmani_cod")healGroup:Int?,
    ): DrugListResponse


    @GET("drugs/{cod}")
    suspend fun getDrugDetail(
        @Path("cod") cod: Int
    ): DrugModels


    @GET("drugs/goroh-darmani")
    suspend fun getGorohDaroei(): DarmanModel


    @GET("Search")
    @Headers(
        "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
        "Accept-Language: en-US,en;q=0.5"
    )
    suspend fun searchDrugs(
        @Query("SearchText") searchText: String
    ): String // Returns raw HTML for the first page
}