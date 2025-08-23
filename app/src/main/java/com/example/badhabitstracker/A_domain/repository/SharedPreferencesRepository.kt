package com.example.badhabitstracker.A_domain.repository

import com.example.badhabitstracker.A_domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SharedPreferencesRepository {

    // Internal session storage (used by UserRepository only)
    suspend fun saveUserId(userId: Long)
    suspend fun getUserId(): Long?
    suspend fun clearUserId()
    suspend fun isUserSessionActive(): Boolean

    // user settings
    suspend fun saveUserSettings(userId: Long, settings: UserSettings)
    suspend fun getUserSettings(userId: Long): UserSettings
    fun getUserSettingsFlow(userId: Long): Flow<UserSettings>

    // onboarding & first launch
    suspend fun setFirstLaunch(isFirst: Boolean)
    suspend fun isFirstLaunch(): Boolean
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun isOnboardingCompleted(): Boolean

    // individual settings (convenience methods)
    suspend fun setNotificationsEnabled(userId: Long, enabled: Boolean)
    suspend fun areNotificationsEnabled(userId: Long): Boolean

    suspend fun setDarkModeEnabled(userId: Long, enabled: Boolean)
    suspend fun isDarkModeEnabled(userId: Long): Boolean

    suspend fun setDailyReminderTime(userId: Long, time: String)
    suspend fun getDailyReminderTime(userId: Long): String

    // cache for offline content
    suspend fun setLastQuoteSync(userId: Long, timestamp: Long)
    suspend fun getLastQuoteSync(userId: Long): Long

    suspend fun setLastHealthTipSync(userId: Long, timestamp: Long)
    suspend fun getLastHealthTipSync(userId: Long): Long

    // cache for statistics
    suspend fun saveTotalHabitsCount(userId: Long, count: Int)
    suspend fun getTotalHabitsCount(userId: Long): Int

    suspend fun saveCurrentStreak(userId: Long, habitId: Long, streak: Int)
    suspend fun getCurrentStreak(userId: Long, habitId: Long): Int

    // cleanup operations
    suspend fun clearUserData(userId: Long)
    suspend fun clearAllData()
}