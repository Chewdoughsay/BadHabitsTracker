package com.example.badhabitstracker.B_data.network

import android.content.Context
import android.content.SharedPreferences
import com.example.badhabitstracker.B_data.repository.QuoteRepositoryImpl
import com.example.badhabitstracker.A_domain.repository.QuoteRepository
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Provides network-related dependencies for HTTP requests
 * This satisfies the "HTTP requests (min 2)" requirement
 */
object NetworkProvider {

    /**
     * Creates HTTP client with logging for development
     */
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Creates Retrofit instance for quotes API
     */
    fun provideQuotesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.QUOTES_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Creates Retrofit instance for facts API
     */
    fun provideFactsRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.FACTS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Creates quotes API service
     */
    fun provideQuotesApiService(retrofit: Retrofit): QuotesApiService {
        return retrofit.create(QuotesApiService::class.java)
    }

    /**
     * Creates facts API service
     */
    fun provideFactsApiService(retrofit: Retrofit): FactsApiService {
        return retrofit.create(FactsApiService::class.java)
    }

    /**
     * Creates SharedPreferences for caching
     */
    fun provideQuotePreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("quote_cache", Context.MODE_PRIVATE)
    }

    /**
     * Creates the complete QuoteRepository with all dependencies
     */
    fun provideQuoteRepository(context: Context): QuoteRepository {
        val okHttpClient = provideOkHttpClient()
        val quotesRetrofit = provideQuotesRetrofit(okHttpClient)
        val factsRetrofit = provideFactsRetrofit(okHttpClient)

        val quotesApiService = provideQuotesApiService(quotesRetrofit)
        val factsApiService = provideFactsApiService(factsRetrofit)
        val sharedPreferences = provideQuotePreferences(context)
        val gson = Gson()

        return QuoteRepositoryImpl(
            quotesApiService = quotesApiService,
            factsApiService = factsApiService,
            sharedPreferences = sharedPreferences,
            gson = gson
        )
    }
}

/**
 * Usage example:
 *
 * class MainActivity : AppCompatActivity() {
 *     private lateinit var quoteRepository: QuoteRepository
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *
 *         // Initialize repository
 *         quoteRepository = NetworkProvider.provideQuoteRepository(this)
 *
 *         // Use in your Use Cases
 *         // val getDailyQuoteUseCase = GetDailyQuoteUseCase(quoteRepository)
 *     }
 * }
 */