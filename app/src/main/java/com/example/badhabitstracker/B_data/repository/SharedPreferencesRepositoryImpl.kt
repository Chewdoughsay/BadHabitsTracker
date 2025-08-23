package com.example.badhabitstracker.B_data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.badhabitstracker.A_domain.model.UserSettings
import com.example.badhabitstracker.A_domain.repository.SharedPreferencesRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class SharedPreferencesRepositoryImpl(
    context: Context
) : SharedPreferencesRepository {

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "bad_habits_prefs"

        // Session keys
        private const val KEY_CURRENT_USER_ID = "current_user_id"

        // User-specific key prefixes
        private fun userKey(userId: Long, key: String) = "user_${userId}_$key"

        // Global keys (not user-specific)
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

        // User settings keys
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_DAILY_REMINDER_TIME = "daily_reminder_time"
        private const val KEY_WEEKLY_REPORT_ENABLED = "weekly_report_enabled"
        private const val KEY_DARK_MODE_ENABLED = "dark_mode_enabled"
        private const val KEY_ACHIEVEMENT_NOTIFICATIONS = "achievement_notifications"
        private const val KEY_MOTIVATIONAL_QUOTES = "motivational_quotes"
        private const val KEY_DATA_BACKUP_ENABLED = "data_backup_enabled"

        // Cache keys
        private const val KEY_LAST_QUOTE_SYNC = "last_quote_sync"
        private const val KEY_LAST_HEALTH_TIP_SYNC = "last_health_tip_sync"
        private const val KEY_TOTAL_HABITS_COUNT = "total_habits_count"

        // Streak cache (habit-specific)
        private fun streakKey(userId: Long, habitId: Long) = "user_${userId}_habit_${habitId}_streak"
    }

    // ============ SESSION MANAGEMENT ============

    override suspend fun saveUserId(userId: Long) {
        sharedPrefs.edit()
            .putLong(KEY_CURRENT_USER_ID, userId)
            .apply()
    }

    override suspend fun getUserId(): Long? {
        val userId = sharedPrefs.getLong(KEY_CURRENT_USER_ID, -1L)
        return if (userId == -1L) null else userId
    }

    override suspend fun clearUserId() {
        sharedPrefs.edit()
            .remove(KEY_CURRENT_USER_ID)
            .apply()
    }

    override suspend fun isUserSessionActive(): Boolean {
        return sharedPrefs.contains(KEY_CURRENT_USER_ID)
    }

    // ============ USER SETTINGS ============

    override suspend fun saveUserSettings(userId: Long, settings: UserSettings) {
        sharedPrefs.edit()
            .putBoolean(userKey(userId, KEY_NOTIFICATIONS_ENABLED), settings.notificationsEnabled)
            .putString(userKey(userId, KEY_DAILY_REMINDER_TIME), settings.dailyReminderTime)
            .putBoolean(userKey(userId, KEY_WEEKLY_REPORT_ENABLED), settings.weeklyReportEnabled)
            .putBoolean(userKey(userId, KEY_DARK_MODE_ENABLED), settings.darkModeEnabled)
            .putBoolean(userKey(userId, KEY_ACHIEVEMENT_NOTIFICATIONS), settings.achievementNotifications)
            .putBoolean(userKey(userId, KEY_MOTIVATIONAL_QUOTES), settings.motivationalQuotes)
            .putBoolean(userKey(userId, KEY_DATA_BACKUP_ENABLED), settings.dataBackupEnabled)
            .apply()
    }

    override suspend fun getUserSettings(userId: Long): UserSettings {
        val defaults = UserSettings()

        return UserSettings(
            notificationsEnabled = sharedPrefs.getBoolean(
                userKey(userId, KEY_NOTIFICATIONS_ENABLED),
                defaults.notificationsEnabled
            ),
            dailyReminderTime = sharedPrefs.getString(
                userKey(userId, KEY_DAILY_REMINDER_TIME),
                defaults.dailyReminderTime
            ) ?: defaults.dailyReminderTime,
            weeklyReportEnabled = sharedPrefs.getBoolean(
                userKey(userId, KEY_WEEKLY_REPORT_ENABLED),
                defaults.weeklyReportEnabled
            ),
            darkModeEnabled = sharedPrefs.getBoolean(
                userKey(userId, KEY_DARK_MODE_ENABLED),
                defaults.darkModeEnabled
            ),
            achievementNotifications = sharedPrefs.getBoolean(
                userKey(userId, KEY_ACHIEVEMENT_NOTIFICATIONS),
                defaults.achievementNotifications
            ),
            motivationalQuotes = sharedPrefs.getBoolean(
                userKey(userId, KEY_MOTIVATIONAL_QUOTES),
                defaults.motivationalQuotes
            ),
            dataBackupEnabled = sharedPrefs.getBoolean(
                userKey(userId, KEY_DATA_BACKUP_ENABLED),
                defaults.dataBackupEnabled
            )
        )
    }

    override fun getUserSettingsFlow(userId: Long): Flow<UserSettings> {
        return callbackFlow {
            // Create listener for SharedPreferences changes
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                // Only emit if the changed key belongs to this user
                if (key?.startsWith("user_${userId}_") == true) {
                    // Emit current settings when any user setting changes
                    val currentSettings = runCatching {
                        kotlinx.coroutines.runBlocking { getUserSettings(userId) }
                    }.getOrNull()

                    if (currentSettings != null) {
                        trySend(currentSettings)
                    }
                }
            }

            // Register listener
            sharedPrefs.registerOnSharedPreferenceChangeListener(listener)

            // Emit initial value
            val initialSettings = runCatching {
                kotlinx.coroutines.runBlocking { getUserSettings(userId) }
            }.getOrNull()

            if (initialSettings != null) {
                trySend(initialSettings)
            }

            // Clean up when Flow is cancelled
            awaitClose {
                sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }.distinctUntilChanged()
    }

    // ============ ONBOARDING & FIRST LAUNCH ============

    override suspend fun setFirstLaunch(isFirst: Boolean) {
        sharedPrefs.edit()
            .putBoolean(KEY_FIRST_LAUNCH, isFirst)
            .apply()
    }

    override suspend fun isFirstLaunch(): Boolean {
        return sharedPrefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        sharedPrefs.edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, completed)
            .apply()
    }

    override suspend fun isOnboardingCompleted(): Boolean {
        return sharedPrefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    // ============ INDIVIDUAL SETTINGS ============

    override suspend fun setNotificationsEnabled(userId: Long, enabled: Boolean) {
        sharedPrefs.edit()
            .putBoolean(userKey(userId, KEY_NOTIFICATIONS_ENABLED), enabled)
            .apply()
    }

    override suspend fun areNotificationsEnabled(userId: Long): Boolean {
        return sharedPrefs.getBoolean(userKey(userId, KEY_NOTIFICATIONS_ENABLED), true)
    }

    override suspend fun setDarkModeEnabled(userId: Long, enabled: Boolean) {
        sharedPrefs.edit()
            .putBoolean(userKey(userId, KEY_DARK_MODE_ENABLED), enabled)
            .apply()
    }

    override suspend fun isDarkModeEnabled(userId: Long): Boolean {
        return sharedPrefs.getBoolean(userKey(userId, KEY_DARK_MODE_ENABLED), false)
    }

    override suspend fun setDailyReminderTime(userId: Long, time: String) {
        sharedPrefs.edit()
            .putString(userKey(userId, KEY_DAILY_REMINDER_TIME), time)
            .apply()
    }

    override suspend fun getDailyReminderTime(userId: Long): String {
        return sharedPrefs.getString(userKey(userId, KEY_DAILY_REMINDER_TIME), "20:00") ?: "20:00"
    }

    // ============ CACHE FOR OFFLINE CONTENT ============

    override suspend fun setLastQuoteSync(userId: Long, timestamp: Long) {
        sharedPrefs.edit()
            .putLong(userKey(userId, KEY_LAST_QUOTE_SYNC), timestamp)
            .apply()
    }

    override suspend fun getLastQuoteSync(userId: Long): Long {
        return sharedPrefs.getLong(userKey(userId, KEY_LAST_QUOTE_SYNC), 0L)
    }

    override suspend fun setLastHealthTipSync(userId: Long, timestamp: Long) {
        sharedPrefs.edit()
            .putLong(userKey(userId, KEY_LAST_HEALTH_TIP_SYNC), timestamp)
            .apply()
    }

    override suspend fun getLastHealthTipSync(userId: Long): Long {
        return sharedPrefs.getLong(userKey(userId, KEY_LAST_HEALTH_TIP_SYNC), 0L)
    }

    // ============ CACHE FOR STATISTICS ============

    override suspend fun saveTotalHabitsCount(userId: Long, count: Int) {
        sharedPrefs.edit()
            .putInt(userKey(userId, KEY_TOTAL_HABITS_COUNT), count)
            .apply()
    }

    override suspend fun getTotalHabitsCount(userId: Long): Int {
        return sharedPrefs.getInt(userKey(userId, KEY_TOTAL_HABITS_COUNT), 0)
    }

    override suspend fun saveCurrentStreak(userId: Long, habitId: Long, streak: Int) {
        sharedPrefs.edit()
            .putInt(streakKey(userId, habitId), streak)
            .apply()
    }

    override suspend fun getCurrentStreak(userId: Long, habitId: Long): Int {
        return sharedPrefs.getInt(streakKey(userId, habitId), 0)
    }

    // ============ CLEANUP OPERATIONS ============

    override suspend fun clearUserData(userId: Long) {
        val editor = sharedPrefs.edit()

        // Get all keys and remove user-specific ones
        sharedPrefs.all.keys.forEach { key ->
            if (key.startsWith("user_${userId}_")) {
                editor.remove(key)
            }
        }

        editor.apply()
    }

    override suspend fun clearAllData() {
        sharedPrefs.edit()
            .clear()
            .apply()
    }
}