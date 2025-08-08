package com.example.badhabitstracker.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserSettings(
    val notificationsEnabled: Boolean = true,
    val dailyReminderTime: String = "20:00",
    val weeklyReportEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val achievementNotifications: Boolean = true,
    val motivationalQuotes: Boolean = true,
    val dataBackupEnabled: Boolean = false
) : Parcelable