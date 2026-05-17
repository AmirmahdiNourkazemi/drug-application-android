package com.approagency.drug.data.remote

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface DarooyabApiService {
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