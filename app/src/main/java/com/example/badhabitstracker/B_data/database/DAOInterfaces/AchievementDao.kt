package com.example.badhabitstracker.B_data.database

import androidx.room.*
import com.example.badhabitstracker.B_data.database.entities.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {

    // Basic CRUD operations
    @Insert(onConflict = OnConflictStrategy.IGNORE) // Ignore if achievement already exists
    suspend fun insertAchievement(achievement: AchievementEntity): Long

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Delete
    suspend fun deleteAchievement(achievement: AchievementEntity)

    // Query operations
    @Query("SELECT * FROM achievements WHERE achievement_id = :achievementId")
    suspend fun getAchievementById(achievementId: Long): AchievementEntity?

    @Query("SELECT * FROM achievements WHERE user_id = :userId ORDER BY unlocked_at DESC")
    fun getAllAchievements(userId: Long): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE habit_id = :habitId ORDER BY unlocked_at DESC")
    fun getAchievementsForHabit(habitId: Long): Flow<List<AchievementEntity>>

    // Achievement checking and unlocking
    @Query("SELECT COUNT(*) > 0 FROM achievements WHERE habit_id = :habitId AND type = :type")
    suspend fun isAchievementUnlocked(habitId: Long, type: String): Boolean

    @Query("SELECT * FROM achievements WHERE habit_id = :habitId AND type = :type LIMIT 1")
    suspend fun getAchievementByHabitAndType(habitId: Long, type: String): AchievementEntity?

    // Mark as viewed operations
    @Query("UPDATE achievements SET is_viewed = 1 WHERE achievement_id = :achievementId")
    suspend fun markAchievementAsViewed(achievementId: Long)

    @Query("UPDATE achievements SET is_viewed = 1 WHERE user_id = :userId")
    suspend fun markAllAchievementsAsViewed(userId: Long)

    // Statistics and counts
    @Query("SELECT COUNT(*) FROM achievements WHERE user_id = :userId")
    suspend fun getTotalAchievementsCount(userId: Long): Int

    @Query("SELECT COUNT(*) FROM achievements WHERE user_id = :userId AND is_viewed = 0")
    suspend fun getUnviewedAchievementsCount(userId: Long): Int

    @Query("SELECT COUNT(*) FROM achievements WHERE habit_id = :habitId")
    suspend fun getAchievementsCountForHabit(habitId: Long): Int

    // Recent achievements for dashboard
    @Query("""
        SELECT * FROM achievements 
        WHERE user_id = :userId 
        ORDER BY unlocked_at DESC 
        LIMIT :limit
    """)
    suspend fun getRecentAchievements(userId: Long, limit: Int): List<AchievementEntity>

    // Recent unviewed achievements for notifications
    @Query("""
        SELECT * FROM achievements 
        WHERE user_id = :userId AND is_viewed = 0 
        ORDER BY unlocked_at DESC 
        LIMIT :limit
    """)
    suspend fun getRecentUnviewedAchievements(userId: Long, limit: Int): List<AchievementEntity>

    // Achievement type statistics
    @Query("""
        SELECT type, COUNT(*) as count 
        FROM achievements 
        WHERE user_id = :userId 
        GROUP BY type 
        ORDER BY count DESC
    """)
    suspend fun getAchievementTypeDistribution(userId: Long): List<AchievementTypeResult>

    // Time-based queries
    @Query("""
        SELECT * FROM achievements 
        WHERE user_id = :userId 
        AND unlocked_at >= :startDate 
        AND unlocked_at <= :endDate 
        ORDER BY unlocked_at DESC
    """)
    fun getAchievementsInDateRange(userId: Long, startDate: Long, endDate: Long): Flow<List<AchievementEntity>>

    // Recent achievements (last 24 hours) for showing "NEW" badges
    @Query("""
        SELECT * FROM achievements 
        WHERE user_id = :userId 
        AND unlocked_at >= :twentyFourHoursAgo 
        ORDER BY unlocked_at DESC
    """)
    suspend fun getRecentNewAchievements(userId: Long, twentyFourHoursAgo: Long): List<AchievementEntity>

    // Search achievements
    @Query("""
        SELECT * FROM achievements 
        WHERE user_id = :userId 
        AND (title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%')
        ORDER BY unlocked_at DESC
    """)
    fun searchAchievements(userId: Long, searchQuery: String): Flow<List<AchievementEntity>>

    // Cleanup operations
    @Query("DELETE FROM achievements WHERE habit_id = :habitId")
    suspend fun deleteAllAchievementsForHabit(habitId: Long)

    @Query("DELETE FROM achievements WHERE user_id = :userId")
    suspend fun deleteAllAchievementsForUser(userId: Long)

    @Query("DELETE FROM achievements WHERE achievement_id = :achievementId")
    suspend fun deleteAchievementById(achievementId: Long)

    // Bulk operations for performance
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>): List<Long>

    @Query("DELETE FROM achievements WHERE achievement_id IN (:achievementIds)")
    suspend fun deleteAchievementsByIds(achievementIds: List<Long>)
}

// Data class for achievement type distribution query result
data class AchievementTypeResult(
    val type: String,
    val count: Int
)