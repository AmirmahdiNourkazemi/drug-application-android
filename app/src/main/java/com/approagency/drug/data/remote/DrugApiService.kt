package com.approagency.drug.data.remote

import com.approagency.drug.data.dto.DrugListResponse
import com.approagency.drug.data.dto.DrugModels
import retrofit2.http.GET
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
}