package com.example.badhabitstracker.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * user cu toate habiturile lui (pt profil)
 */
@Parcelize
data class UserWithHabits(
    val user: User,
    val habits: List<Habit>
) : Parcelable

/**
 * habit cu toate entryurile lui (pentru statistici si afisare detaliata)
 */
@Parcelize
data class HabitWithEntries(
    val habit: Habit,
    val entries: List<HabitEntry>
) : Parcelable {

    /**
     * calc rata de succes pt un habit
     */
    fun getSuccessRate(): Double {
        if (entries.isEmpty()) return 0.0
        val successfulCount = entries.count { it.wasSuccessful }
        return (successfulCount.toDouble() / entries.size) * 100
    }

    /**
     * calculeaza streakul curent
     */
    fun getCurrentStreak(): Int {
        val sortedEntries = entries.sortedByDescending { it.date }
        var streak = 0

        for (entry in sortedEntries) {
            if (entry.wasSuccessful) {
                streak++
            } else {
                break
            }
        }

        return streak
    }
}

/**
 * habit cu toate achievementurile lui
 */
@Parcelize
data class HabitWithAchievements(
    val habit: Habit,
    val achievements: List<Achievement>
) : Parcelable

/**
 * user dashboard complet
 */
@Parcelize
data class UserDashboard(
    val user: User,
    val activeHabits: List<Habit>,
    val recentAchievements: List<Achievement>,
    val totalSuccessfulDays: Int,
    val totalMoneySaved: Double
) : Parcelable