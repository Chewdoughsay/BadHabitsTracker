package com.example.badhabitstracker.data.network

import retrofit2.http.GET

interface FactsApiService {

    @GET("random.json")
    suspend fun getRandomFact(): FactResponse
}

// Response model for facts API
data class FactResponse(
    val text: String,
    val source: String? = null,
    val source_url: String? = null,
    val language: String? = null
)

object ApiConstants {
    const val QUOTES_BASE_URL = "https://api.quotable.io/"
    const val FACTS_BASE_URL = "https://uselessfacts.jsph.pl/"
}