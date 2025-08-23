package com.example.badhabitstracker.data.repository

import android.content.SharedPreferences
import com.example.badhabitstracker.data.network.FactsApiService
import com.example.badhabitstracker.data.network.QuotesApiService
import com.example.badhabitstracker.domain.repository.HealthTip
import com.example.badhabitstracker.domain.repository.MotivationalQuote
import com.example.badhabitstracker.domain.repository.QuoteRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class QuoteRepositoryImpl(
    private val quotesApiService: QuotesApiService,
    private val factsApiService: FactsApiService,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : QuoteRepository {

    companion object {
        private const val KEY_CACHED_QUOTES = "cached_quotes"
        private const val KEY_CACHED_HEALTH_TIPS = "cached_health_tips"
        private const val KEY_DAILY_QUOTE_DATE = "daily_quote_date"
        private const val KEY_DAILY_QUOTE_CONTENT = "daily_quote_content"
        private const val KEY_DAILY_HEALTH_TIP_DATE = "daily_health_tip_date"
        private const val KEY_DAILY_HEALTH_TIP_CONTENT = "daily_health_tip_content"

        private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

    // ============ MOTIVATIONAL QUOTES API (HTTP Request #1) ============

    override suspend fun getRandomQuote(): Result<MotivationalQuote> {
        return try {
            val response = quotesApiService.getRandomQuote()
            val quote = MotivationalQuote(
                content = response.content,
                author = response.author
            )
            Result.success(quote)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getQuotesByCategory(category: String): Result<List<MotivationalQuote>> {
        return try {
            val response = quotesApiService.getQuotesByTag(category)
            val quotes = response.results.map { quoteResponse ->
                MotivationalQuote(
                    content = quoteResponse.content,
                    author = quoteResponse.author
                )
            }
            Result.success(quotes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ HEALTH FACTS API (HTTP Request #2) ============

    override suspend fun getRandomHealthTip(): Result<HealthTip> {
        return try {
            val response = factsApiService.getRandomFact()
            val healthTip = HealthTip(
                fact = response.text,
                category = "general" // API doesn't provide categories, so we use default
            )
            Result.success(healthTip)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHealthTipsForCategory(category: String): Result<List<HealthTip>> {
        return try {
            // Since the free API doesn't support categories, we'll get multiple random facts
            val tips = mutableListOf<HealthTip>()
            repeat(5) { // Get 5 random facts as a "category"
                val response = factsApiService.getRandomFact()
                tips.add(HealthTip(
                    fact = response.text,
                    category = category
                ))
            }
            Result.success(tips)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ CACHE MANAGEMENT ============

    override suspend fun getCachedQuotes(): List<MotivationalQuote> {
        val quotesJson = sharedPreferences.getString(KEY_CACHED_QUOTES, null)
        return if (quotesJson != null) {
            try {
                val type = object : TypeToken<List<MotivationalQuote>>() {}.type
                gson.fromJson(quotesJson, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    override suspend fun saveCachedQuotes(quotes: List<MotivationalQuote>) {
        try {
            val quotesJson = gson.toJson(quotes)
            sharedPreferences.edit()
                .putString(KEY_CACHED_QUOTES, quotesJson)
                .apply()
        } catch (e: Exception) {
            // Silently fail - caching is not critical
        }
    }

    override suspend fun getCachedHealthTips(): List<HealthTip> {
        val tipsJson = sharedPreferences.getString(KEY_CACHED_HEALTH_TIPS, null)
        return if (tipsJson != null) {
            try {
                val type = object : TypeToken<List<HealthTip>>() {}.type
                gson.fromJson(tipsJson, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    override suspend fun saveCachedHealthTips(tips: List<HealthTip>) {
        try {
            val tipsJson = gson.toJson(tips)
            sharedPreferences.edit()
                .putString(KEY_CACHED_HEALTH_TIPS, tipsJson)
                .apply()
        } catch (e: Exception) {
            // Silently fail - caching is not critical
        }
    }

    // ============ DAILY CONTENT (Same content per day) ============

    override suspend fun getDailyQuote(): Result<MotivationalQuote> {
        val today = dateFormatter.format(Date())
        val lastQuoteDate = sharedPreferences.getString(KEY_DAILY_QUOTE_DATE, null)

        // Check if we already have today's quote
        if (lastQuoteDate == today) {
            val cachedQuoteJson = sharedPreferences.getString(KEY_DAILY_QUOTE_CONTENT, null)
            if (cachedQuoteJson != null) {
                try {
                    val cachedQuote = gson.fromJson(cachedQuoteJson, MotivationalQuote::class.java)
                    return Result.success(cachedQuote)
                } catch (e: Exception) {
                    // Continue to fetch new quote if parsing fails
                }
            }
        }

        // Fetch new quote for today
        return getRandomQuote().also { result ->
            result.getOrNull()?.let { quote ->
                // Cache today's quote
                try {
                    val quoteJson = gson.toJson(quote)
                    sharedPreferences.edit()
                        .putString(KEY_DAILY_QUOTE_DATE, today)
                        .putString(KEY_DAILY_QUOTE_CONTENT, quoteJson)
                        .apply()
                } catch (e: Exception) {
                    // Silently fail - caching is not critical
                }
            }
        }
    }

    override suspend fun getDailyHealthTip(): Result<HealthTip> {
        val today = dateFormatter.format(Date())
        val lastTipDate = sharedPreferences.getString(KEY_DAILY_HEALTH_TIP_DATE, null)

        // Check if we already have today's health tip
        if (lastTipDate == today) {
            val cachedTipJson = sharedPreferences.getString(KEY_DAILY_HEALTH_TIP_CONTENT, null)
            if (cachedTipJson != null) {
                try {
                    val cachedTip = gson.fromJson(cachedTipJson, HealthTip::class.java)
                    return Result.success(cachedTip)
                } catch (e: Exception) {
                    // Continue to fetch new tip if parsing fails
                }
            }
        }

        // Fetch new health tip for today
        return getRandomHealthTip().also { result ->
            result.getOrNull()?.let { tip ->
                // Cache today's health tip
                try {
                    val tipJson = gson.toJson(tip)
                    sharedPreferences.edit()
                        .putString(KEY_DAILY_HEALTH_TIP_DATE, today)
                        .putString(KEY_DAILY_HEALTH_TIP_CONTENT, tipJson)
                        .apply()
                } catch (e: Exception) {
                    // Silently fail - caching is not critical
                }
            }
        }
    }

    // ============ HELPER METHODS ============

    /**
     * Preloads cache with initial content for offline usage
     */
    suspend fun preloadCache() {
        try {
            // Preload quotes
            val quotesResult = getQuotesByCategory("motivational")
            quotesResult.getOrNull()?.let { quotes ->
                saveCachedQuotes(quotes)
            }

            // Preload health tips
            val tipsResult = getHealthTipsForCategory("health")
            tipsResult.getOrNull()?.let { tips ->
                saveCachedHealthTips(tips)
            }
        } catch (e: Exception) {
            // Silently fail - preloading is not critical
        }
    }

    /**
     * Clears all cached content
     */
    suspend fun clearCache() {
        sharedPreferences.edit()
            .remove(KEY_CACHED_QUOTES)
            .remove(KEY_CACHED_HEALTH_TIPS)
            .remove(KEY_DAILY_QUOTE_DATE)
            .remove(KEY_DAILY_QUOTE_CONTENT)
            .remove(KEY_DAILY_HEALTH_TIP_DATE)
            .remove(KEY_DAILY_HEALTH_TIP_CONTENT)
            .apply()
    }
}