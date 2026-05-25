package com.approagency.drug.data.remote

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface DarooyabApiService {
    @FormUrlEncoded
    @POST("Home/PartialNewSearch")
    @Headers(
        "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36",
        "X-Requested-With: XMLHttpRequest",
        "Accept: */*",
        "Accept-Language: en-US,en;q=0.9,fa;q=0.8"
    )
    suspend fun searchDrugs(
        @Field("autocomplete") searchText: String,
        @Field("DrugName_pageNumber") pageNumber: Int = 1
    ): String
}