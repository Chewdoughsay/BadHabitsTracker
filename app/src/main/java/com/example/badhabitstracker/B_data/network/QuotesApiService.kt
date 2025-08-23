package com.example.badhabitstracker.B_data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface QuotesApiService {

    @GET("random")
    suspend fun getRandomQuote(): QuoteResponse

    @GET("quotes")
    suspend fun getQuotesByTag(
        @Query("tags") tag: String,
        @Query("limit") limit: Int = 10
    ): QuotesListResponse
}

// Response models for quotes API
data class QuoteResponse(
    val content: String,
    val author: String,
    val tags: List<String>? = null
)

data class QuotesListResponse(
    val results: List<QuoteResponse>,
    val count: Int,
    val totalCount: Int
)