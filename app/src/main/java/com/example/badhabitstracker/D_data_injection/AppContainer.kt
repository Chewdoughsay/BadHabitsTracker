package com.example.badhabitstracker.D_data_injection

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.badhabitstracker.B_data.database.DatabaseProvider
import com.example.badhabitstracker.B_data.network.NetworkProvider
import com.example.badhabitstracker.B_data.repository.*
import com.example.badhabitstracker.A_domain.repository.*
import com.example.badhabitstracker.A_domain.usecase.authentification.*
import com.example.badhabitstracker.C_presentation.viewmodel.SplashViewModel

/**
 * App-wide dependency container
 * Creates and manages all dependencies in one place
 */
class AppContainer(context: Context) {

    // ============ DATA LAYER ============

    // Database
    private val database = DatabaseProvider.getDatabase(context)

    // DAOs
    private val userDao = database.userDao()
    private val habitDao = database.habitDao()
    private val habitEntryDao = database.habitEntryDao()
    private val achievementDao = database.achievementDao()

    // SharedPreferences
    private val sharedPrefsRepository: SharedPreferencesRepository =
        SharedPreferencesRepositoryImpl(context)

    // Network
    private val quoteRepository: QuoteRepository =
        NetworkProvider.provideQuoteRepository(context)

    // ============ REPOSITORIES ============

    val userRepository: UserRepository =
        UserRepositoryImpl(userDao, sharedPrefsRepository)

    val habitRepository: HabitRepository =
        HabitRepositoryImpl(habitDao, habitEntryDao)

    val achievementRepository: AchievementRepository =
        AchievementRepositoryImpl(achievementDao)

    // ============ USE CASES ============

    // Authentication Use Cases
    val isUserLoggedInUseCase = IsUserLoggedInUseCase(userRepository)
    val loginUserUseCase = LoginUserUseCase(userRepository)
    val registerUserUseCase = RegisterUserUseCase(userRepository)
    val logoutUserUseCase = LogoutUserUseCase(userRepository)
    val getCurrentUserUseCase = GetCurrentUserUseCase(userRepository)

    // ============ VIEWMODEL FACTORY ============

    /**
     * Creates ViewModels with their dependencies automatically
     */
    val viewModelFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SplashViewModel::class.java -> {
                    SplashViewModel(isUserLoggedInUseCase) as T
                }
                // Future ViewModels go here:
                // LoginViewModel::class.java -> LoginViewModel(loginUserUseCase) as T
                // DashboardViewModel::class.java -> DashboardViewModel(getHabitsUseCase) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}

/**
 * Application class to hold the container
 */
class BadHabitsApplication : Application() {
    val appContainer by lazy { AppContainer(this) }
}

/**
 * Extension function to get container from any Context
 */
val Context.appContainer: AppContainer
    get() = (applicationContext as BadHabitsApplication).appContainer