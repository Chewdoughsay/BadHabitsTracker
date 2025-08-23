package com.example.badhabitstracker.A_domain.repository

data class MotivationalQuote(
    val content: String,
    val author: String
)

data class HealthTip(
    val fact: String,
    val category: String? = null
)

interface QuoteRepository {
    // motivational quotes API (HTTP Request #1)
    suspend fun getRandomQuote(): Result<MotivationalQuote>
    suspend fun getQuotesByCategory(category: String): Result<List<MotivationalQuote>>

    // health facts API (HTTP Request #2)
    suspend fun getRandomHealthTip(): Result<HealthTip>
    suspend fun getHealthTipsForCategory(category: String): Result<List<HealthTip>>

    // cache management
    suspend fun getCachedQuotes(): List<MotivationalQuote>
    suspend fun saveCachedQuotes(quotes: List<MotivationalQuote>)
    suspend fun getCachedHealthTips(): List<HealthTip>
    suspend fun saveCachedHealthTips(tips: List<HealthTip>)

    // daily content
    suspend fun getDailyQuote(): Result<MotivationalQuote>
    suspend fun getDailyHealthTip(): Result<HealthTip>
}