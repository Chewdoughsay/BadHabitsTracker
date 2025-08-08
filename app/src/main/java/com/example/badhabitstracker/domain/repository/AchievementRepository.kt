package com.example.badhabitstracker.domain.repository

import com.example.badhabitstracker.domain.model.Achievement
import com.example.badhabitstracker.domain.model.AchievementType
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    // operatii CRUD
    suspend fun insertAchievement(achievement: Achievement): Long
    suspend fun updateAchievement(achievement: Achievement)
    suspend fun deleteAchievement(achievement: Achievement)

    // achievement queries
    fun getAllAchievements(): Flow<List<Achievement>>
    fun getAchievementsForHabit(habitId: Long): Flow<List<Achievement>>
    suspend fun getAchievementById(id: Long): Achievement?

    // achievement logic
    suspend fun checkAndUnlockAchievements(habitId: Long, currentStreak: Int, isFirstHabit: Boolean)
    suspend fun isAchievementUnlocked(habitId: Long, type: AchievementType): Boolean
    suspend fun markAchievementAsViewed(achievementId: Long)

    // statistics
    suspend fun getUnviewedAchievementsCount(): Int
    suspend fun getRecentAchievements(limit: Int = 5): List<Achievement>
    suspend fun getTotalAchievementsCount(): Int
}