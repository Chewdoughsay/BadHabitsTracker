package com.example.badhabitstracker.domain.usecase

import com.example.badhabitstracker.domain.repository.MotivationalQuote
import com.example.badhabitstracker.domain.repository.HealthTip
import com.example.badhabitstracker.domain.repository.QuoteRepository

/**
 * HTTP Request #1: Get daily motivational quote
 */
class GetDailyQuoteUseCase(
    private val quoteRepository: QuoteRepository
) : BaseUseCaseNoParams<MotivationalQuote>() {

    override suspend fun execute(): MotivationalQuote {

        // Try to get daily quote (cached if recent)
        val result = quoteRepository.getDailyQuote()

        return result.getOrElse { exception ->
            // Fallback to cached quotes if network fails
            val cachedQuotes = quoteRepository.getCachedQuotes()
            if (cachedQuotes.isNotEmpty()) {
                cachedQuotes.random()
            } else {
                throw exception
            }
        }
    }
}

/**
 * Get random motivational quote with fallback (HTTP Request #1 alternative)
 */
class GetRandomQuoteUseCase(
    private val quoteRepository: QuoteRepository
) : BaseUseCaseNoParams<MotivationalQuote>() {

    override suspend fun execute(): MotivationalQuote {

        val result = quoteRepository.getRandomQuote()

        return result.getOrElse { exception ->
            // Fallback to cached quotes
            val cachedQuotes = quoteRepository.getCachedQuotes()
            if (cachedQuotes.isNotEmpty()) {
                cachedQuotes.random()
            } else {
                throw exception
            }
        }
    }
}

/**
 * Get daily health tip (HTTP Request #2)
 */
class GetDailyHealthTipUseCase(
    private val quoteRepository: QuoteRepository
) : BaseUseCaseNoParams<HealthTip>() {

    override suspend fun execute(): HealthTip {

        val result = quoteRepository.getDailyHealthTip()

        return result.getOrElse { exception ->
            // Fallback to cached health tips
            val cachedTips = quoteRepository.getCachedHealthTips()
            if (cachedTips.isNotEmpty()) {
                cachedTips.random()
            } else {
                throw exception
            }
        }
    }
}

/**
 * Get random health tip with category (HTTP Request #2 alternative)
 */
data class HealthTipParams(
    val category: String? = null
)

class GetRandomHealthTipUseCase(
    private val quoteRepository: QuoteRepository
) : BaseUseCase<HealthTipParams, HealthTip>() {

    override suspend fun execute(parameters: HealthTipParams): HealthTip {

        val result = if (parameters.category != null) {
            quoteRepository.getHealthTipsForCategory(parameters.category)
                .map { tips -> tips.randomOrNull() ?: throw Exception("No tips found for category") }
        } else {
            quoteRepository.getRandomHealthTip()
        }

        return result.getOrElse { exception ->
            // Fallback to cached tips
            val cachedTips = quoteRepository.getCachedHealthTips()
            val filteredTips = if (parameters.category != null) {
                cachedTips.filter { it.category == parameters.category }
            } else {
                cachedTips
            }

            if (filteredTips.isNotEmpty()) {
                filteredTips.random()
            } else {
                throw exception
            }
        }
    }
}

/**
 * Sync and cache motivational content (for offline usage)
 */
class SyncMotivationalContentUseCase(
    private val quoteRepository: QuoteRepository
) : BaseUseCaseNoParams<Unit>() {

    override suspend fun execute() {

        try {
            // Fetch fresh quotes and cache them
            val quotesResult = quoteRepository.getQuotesByCategory("motivation")
            quotesResult.getOrNull()?.let { quotes ->
                quoteRepository.saveCachedQuotes(quotes)
            }

            // Fetch fresh health tips and cache them
            val tipsResult = quoteRepository.getHealthTipsForCategory("general")
            tipsResult.getOrNull()?.let { tips ->
                quoteRepository.saveCachedHealthTips(tips)
            }

        } catch (e: Exception) {
            // Ignore errors in background sync
            // Existing cache will continue to work
        }
    }
}