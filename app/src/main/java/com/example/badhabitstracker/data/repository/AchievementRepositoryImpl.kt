package com.example.badhabitstracker.data.repository

import com.example.badhabitstracker.data.database.AchievementDao
import com.example.badhabitstracker.data.database.entities.toDomain
import com.example.badhabitstracker.data.database.entities.toEntity
import com.example.badhabitstracker.domain.model.Achievement
import com.example.badhabitstracker.domain.model.AchievementType
import com.example.badhabitstracker.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Date

class AchievementRepositoryImpl(
    private val achievementDao: AchievementDao
) : AchievementRepository {

    // ============ BASIC CRUD OPERATIONS ============

    override suspend fun insertAchievement(achievement: Achievement): Long {
        return achievementDao.insertAchievement(achievement.toEntity())
    }

    override suspend fun updateAchievement(achievement: Achievement) {
        achievementDao.updateAchievement(achievement.toEntity())
    }

    override suspend fun deleteAchievement(achievement: Achievement) {
        achievementDao.deleteAchievement(achievement.toEntity())
    }

    // ============ ACHIEVEMENT QUERIES ============

    override fun getAllAchievements(userId: Long): Flow<List<Achievement>> {
        return achievementDao.getAllAchievements(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAchievementsForHabit(habitId: Long): Flow<List<Achievement>> {
        return achievementDao.getAchievementsForHabit(habitId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAchievementById(id: Long): Achievement? {
        return achievementDao.getAchievementById(id)?.toDomain()
    }

    // ============ ACHIEVEMENT LOGIC - CORE BUSINESS LOGIC ============

    override suspend fun checkAndUnlockAchievements(
        habitId: Long,
        userId: Long,
        currentStreak: Int,
        isFirstHabit: Boolean
    ) {
        val achievementsToUnlock = mutableListOf<Achievement>()

        // Check each achievement type to see if it should be unlocked
        val potentialAchievements = listOf(
            AchievementType.FIRST_DAY to (currentStreak >= 1),
            AchievementType.WEEK_STREAK to (currentStreak >= 7),
            AchievementType.MONTH_STREAK to (currentStreak >= 30),
            AchievementType.HUNDRED_DAYS to (currentStreak >= 100),
            AchievementType.FIRST_HABIT to isFirstHabit
        )

        for ((achievementType, shouldUnlock) in potentialAchievements) {
            if (shouldUnlock && !isAchievementUnlocked(habitId, achievementType)) {
                val achievement = Achievement.createMilestone(
                    habitId = habitId,
                    userId = userId,
                    type = achievementType
                )
                achievementsToUnlock.add(achievement)
            }
        }

        // Special logic for COMEBACK achievement
        if (currentStreak == 1) {
            // Check if user had achievements before (indicating a restart)
            val previousAchievements = achievementDao.getAchievementsForHabit(habitId).first()
            if (previousAchievements.isNotEmpty() && !isAchievementUnlocked(habitId, AchievementType.COMEBACK)) {
                val comebackAchievement = Achievement.createMilestone(
                    habitId = habitId,
                    userId = userId,
                    type = AchievementType.COMEBACK
                )
                achievementsToUnlock.add(comebackAchievement)
            }
        }

        // Bulk insert all new achievements
        if (achievementsToUnlock.isNotEmpty()) {
            try {
                val entities = achievementsToUnlock.map { it.toEntity() }
                achievementDao.insertAchievements(entities)
            } catch (e: Exception) {
                // Log error but don't throw - achievement unlocking shouldn't break main flow
                // In a real app, you'd use proper logging here
                println("Failed to unlock achievements: ${e.message}")
            }
        }
    }

    override suspend fun isAchievementUnlocked(habitId: Long, type: AchievementType): Boolean {
        return achievementDao.isAchievementUnlocked(habitId, type.name)
    }

    override suspend fun markAchievementAsViewed(achievementId: Long) {
        achievementDao.markAchievementAsViewed(achievementId)
    }

    // ============ STATISTICS ============

    override suspend fun getUnviewedAchievementsCount(userId: Long): Int {
        return achievementDao.getUnviewedAchievementsCount(userId)
    }

    override suspend fun getRecentAchievements(userId: Long, limit: Int): List<Achievement> {
        return achievementDao.getRecentAchievements(userId, limit).map { it.toDomain() }
    }

    override suspend fun getTotalAchievementsCount(userId: Long): Int {
        return achievementDao.getTotalAchievementsCount(userId)
    }

    // ============ ADVANCED ACHIEVEMENT LOGIC (BONUS METHODS) ============

    /**
     * Checks for milestone achievement based on habit target
     */
    suspend fun checkMilestoneAchievement(habitId: Long, userId: Long, targetDays: Int?, currentStreak: Int) {
        if (targetDays != null &&
            currentStreak >= targetDays &&
            !isAchievementUnlocked(habitId, AchievementType.MILESTONE)) {

            val milestoneAchievement = Achievement.createMilestone(
                habitId = habitId,
                userId = userId,
                type = AchievementType.MILESTONE,
                customTitle = "Goal Reached: $targetDays Days!"
            )

            try {
                insertAchievement(milestoneAchievement)
            } catch (e: Exception) {
                println("Failed to unlock milestone achievement: ${e.message}")
            }
        }
    }

    /**
     * Checks for multi-habit achievement across all user's habits
     */
    suspend fun checkMultiHabitAchievement(userId: Long, totalActiveHabits: Int) {
        if (totalActiveHabits >= 3) {
            // For multi-habit achievement, we need to check if ANY habit has it
            // This is a limitation of our current model - we'll use the first habit
            // In a real app, you might want user-level achievements separate from habit-level
            val userAchievements = achievementDao.getAllAchievements(userId).first()
            val hasMultiHabitAchievement = userAchievements.any { it.type == AchievementType.MULTIPLE_HABITS }

            if (!hasMultiHabitAchievement) {
                // Use the first habit ID for this achievement (architectural limitation)
                // In practice, you might want a separate user_achievements table
                println("Multi-habit achievement logic needs architectural refinement")
            }
        }
    }

    /**
     * Gets achievements that are "new" (unlocked in last 24 hours)
     */
    suspend fun getNewAchievements(userId: Long): List<Achievement> {
        val twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        return achievementDao.getRecentNewAchievements(userId, twentyFourHoursAgo)
            .map { it.toDomain() }
    }

    /**
     * Marks all achievements as viewed for a user
     */
    suspend fun markAllAchievementsAsViewed(userId: Long) {
        achievementDao.markAllAchievementsAsViewed(userId)
    }
}