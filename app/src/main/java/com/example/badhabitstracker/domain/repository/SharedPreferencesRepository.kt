package com.example.badhabitstracker.domain.repository

import com.example.badhabitstracker.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SharedPreferencesRepository {

    // user session, pt logare automata daca utilizatorul a ramas logat
    suspend fun saveUserId(userId: Long)
    suspend fun getUserId(): Long?
    suspend fun clearUserId()

    // user settings
    suspend fun saveUserSettings(settings: UserSettings)
    suspend fun getUserSettings(): UserSettings
    fun getUserSettingsFlow(): Flow<UserSettings>

    // onboarding & first launch (vedem daca ajung sa fac si asta)
    suspend fun setFirstLaunch(isFirst: Boolean)
    suspend fun isFirstLaunch(): Boolean
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun isOnboardingCompleted(): Boolean

    // setari/preferinte pt apl
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun areNotificationsEnabled(): Boolean

    suspend fun setDarkModeEnabled(enabled: Boolean)
    suspend fun isDarkModeEnabled(): Boolean

    suspend fun setDailyReminderTime(time: String)
    suspend fun getDailyReminderTime(): String

    // timestampuri pt afisare in offline (probabil nu voi implementa asta)
    suspend fun setLastQuoteSync(timestamp: Long)
    suspend fun getLastQuoteSync(): Long

    suspend fun setLastHealthTipSync(timestamp: Long)
    suspend fun getLastHealthTipSync(): Long

    // cache pt statistici
    suspend fun saveTotalHabitsCount(count: Int)
    suspend fun getTotalHabitsCount(): Int

    suspend fun saveCurrentStreak(habitId: Long, streak: Int)
    suspend fun getCurrentStreak(habitId: Long): Int
}