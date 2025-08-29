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
import com.example.badhabitstracker.A_domain.usecase.*
import com.example.badhabitstracker.C_presentation.viewmodel.DashboardViewModel
import com.example.badhabitstracker.C_presentation.viewmodel.LoginViewModel
import com.example.badhabitstracker.C_presentation.viewmodel.RegisterViewModel
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

    // Dashboard Use Cases
    val getDashboardStatisticsUseCase = GetDashboardStatisticsUseCase(
        habitRepository, achievementRepository, userRepository
    )
    val getUserDashboardUseCase = GetUserDashboardUseCase(
        userRepository, habitRepository, achievementRepository
    )

    // Habit Management Use Cases
    val getHabitsUseCase = GetHabitsUseCase(habitRepository, userRepository)
    val addHabitUseCase = AddHabitUseCase(habitRepository, achievementRepository, userRepository)
    val logDailyProgressUseCase = LogDailyProgressUseCase(
        habitRepository, achievementRepository, userRepository
    )
    val getHabitStatisticsUseCase = GetHabitStatisticsUseCase(habitRepository, userRepository)

    // Achievement Use Cases
    val getAllAchievementsUseCase = GetAllAchievementsUseCase(achievementRepository, userRepository)
    val getRecentAchievementsUseCase = GetRecentAchievementsUseCase(achievementRepository, userRepository)
    val getUnviewedAchievementsCountUseCase = GetUnviewedAchievementsCountUseCase(
        achievementRepository, userRepository
    )

    // HTTP Request Use Cases (Requirements: min 2 HTTP requests)
    val getDailyQuoteUseCase = GetDailyQuoteUseCase(quoteRepository)
    val getDailyHealthTipUseCase = GetDailyHealthTipUseCase(quoteRepository)
    val getRandomQuoteUseCase = GetRandomQuoteUseCase(quoteRepository)

    // Settings Use Cases (SharedPreferences requirement)
    val getUserSettingsUseCase = GetUserSettingsUseCase(sharedPrefsRepository, userRepository)
    val updateUserSettingsUseCase = UpdateUserSettingsUseCase(sharedPrefsRepository, userRepository)
    val updateDarkModeUseCase = UpdateDarkModeUseCase(sharedPrefsRepository, userRepository)
    val updateNotificationSettingsUseCase = UpdateNotificationSettingsUseCase(
        sharedPrefsRepository, userRepository
    )

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

                LoginViewModel::class.java -> {
                    LoginViewModel(loginUserUseCase) as T
                }

                RegisterViewModel::class.java -> {
                    RegisterViewModel(registerUserUseCase) as T
                }

                DashboardViewModel::class.java -> {
                    DashboardViewModel(
                        // Authentication
                        getCurrentUserUseCase = getCurrentUserUseCase,
                        logoutUserUseCase = logoutUserUseCase,

                        // Dashboard Data
                        getDashboardStatisticsUseCase = getDashboardStatisticsUseCase,
                        getHabitsUseCase = getHabitsUseCase,
                        getRecentAchievementsUseCase = getRecentAchievementsUseCase,

                        // HTTP Requests (Requirements)
                        getDailyQuoteUseCase = getDailyQuoteUseCase,
                        getDailyHealthTipUseCase = getDailyHealthTipUseCase,

                        // Settings (SharedPreferences requirement)
                        getUserSettingsUseCase = getUserSettingsUseCase,

                        // Habit Actions
                        logDailyProgressUseCase = logDailyProgressUseCase
                    ) as T
                }

                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}

/**
 * Custom Application class that holds our dependency container
 * This ensures single instance of AppContainer throughout app lifecycle
 */
class BadHabitsApplication : Application() {
    val appContainer by lazy { AppContainer(this) }
}

/**
 * Extension function to easily access AppContainer from any Context
 * Usage: requireContext().appContainer.viewModelFactory
 */
val Context.appContainer: AppContainer
    get() = (applicationContext as BadHabitsApplication).appContainer