package com.example.badhabitstracker.domain.repository

import com.example.badhabitstracker.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SharedPreferencesRepository {

    // user session, pt logare automata daca utilizatorul a ramas logat
    suspend fun saveUserId(userId: Long)
    suspend fun getUserId(): Long?
    suspend fun clearUserId()
    suspend fun isUserSessionActive(): Boolean

    // user settings
    suspend fun saveUserSettings(userId: Long, settings: UserSettings)
    suspend fun getUserSettings(userId: Long): UserSettings
    fun getUserSettingsFlow(userId: Long): Flow<UserSettings>

    // onboarding & first launch (vedem daca ajung sa fac si asta)
    suspend fun setFirstLaunch(isFirst: Boolean)
    suspend fun isFirstLaunch(): Boolean
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun isOnboardingCompleted(): Boolean

    // setari/preferinte pt apl
    suspend fun setNotificationsEnabled(userId: Long, enabled: Boolean)
    suspend fun areNotificationsEnabled(userId: Long): Boolean

    suspend fun setDarkModeEnabled(userId: Long, enabled: Boolean)
    suspend fun isDarkModeEnabled(userId: Long): Boolean

    suspend fun setDailyReminderTime(userId: Long, time: String)
    suspend fun getDailyReminderTime(userId: Long): String

    // timestampuri pt afisare in offline (probabil nu voi implementa asta)
    suspend fun setLastQuoteSync(userId: Long, timestamp: Long)
    suspend fun getLastQuoteSync(userId: Long): Long

    suspend fun setLastHealthTipSync(userId: Long, timestamp: Long)
    suspend fun getLastHealthTipSync(userId: Long): Long

    // cache pt statistici
    suspend fun saveTotalHabitsCount(userId: Long, count: Int)
    suspend fun getTotalHabitsCount(userId: Long): Int

    suspend fun saveCurrentStreak(userId: Long, habitId: Long, streak: Int)
    suspend fun getCurrentStreak(userId: Long, habitId: Long): Int

    // pt logout si stergere cont
    suspend fun clearUserData(userId: Long)
    suspend fun clearAllData()
}