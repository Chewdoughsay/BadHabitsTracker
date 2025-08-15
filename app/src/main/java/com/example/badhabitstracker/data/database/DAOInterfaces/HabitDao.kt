package com.example.badhabitstracker.data.database

import androidx.room.*
import com.example.badhabitstracker.data.database.entities.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    // Basic CRUD operations
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    // Query operations
    @Query("SELECT * FROM habits WHERE habit_id = :habitId")
    suspend fun getHabitById(habitId: Long): HabitEntity?

    @Query("SELECT * FROM habits WHERE user_id = :userId ORDER BY created_at DESC")
    fun getAllHabits(userId: Long): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE user_id = :userId AND is_active = 1 ORDER BY created_at DESC")
    fun getActiveHabits(userId: Long): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE user_id = :userId AND is_active = 0 ORDER BY updated_at DESC")
    fun getCompletedHabits(userId: Long): Flow<List<HabitEntity>>

    // Statistics queries
    @Query("SELECT COUNT(*) FROM habits WHERE user_id = :userId")
    suspend fun getHabitsCount(userId: Long): Int

    @Query("SELECT COUNT(*) FROM habits WHERE user_id = :userId AND is_active = 1")
    suspend fun getActiveHabitsCount(userId: Long): Int

    @Query("SELECT COUNT(*) FROM habits WHERE user_id = :userId AND is_active = 0")
    suspend fun getCompletedHabitsCount(userId: Long): Int

    // Category-based queries
    @Query("SELECT * FROM habits WHERE user_id = :userId AND category = :category ORDER BY created_at DESC")
    fun getHabitsByCategory(userId: Long, category: String): Flow<List<HabitEntity>>

    // Streak and progress updates
    @Query("UPDATE habits SET current_streak = :streak, updated_at = :updatedAt WHERE habit_id = :habitId")
    suspend fun updateCurrentStreak(habitId: Long, streak: Int, updatedAt: Long)

    @Query("UPDATE habits SET longest_streak = :streak, updated_at = :updatedAt WHERE habit_id = :habitId")
    suspend fun updateLongestStreak(habitId: Long, streak: Int, updatedAt: Long)

    @Query("UPDATE habits SET current_streak = :currentStreak, longest_streak = :longestStreak, updated_at = :updatedAt WHERE habit_id = :habitId")
    suspend fun updateStreaks(habitId: Long, currentStreak: Int, longestStreak: Int, updatedAt: Long)

    @Query("UPDATE habits SET is_active = :isActive, updated_at = :updatedAt WHERE habit_id = :habitId")
    suspend fun updateHabitStatus(habitId: Long, isActive: Boolean, updatedAt: Long)

    // Performance-oriented queries for statistics calculations
    @Query("SELECT current_streak FROM habits WHERE habit_id = :habitId")
    suspend fun getCurrentStreak(habitId: Long): Int?

    @Query("SELECT longest_streak FROM habits WHERE habit_id = :habitId")
    suspend fun getLongestStreak(habitId: Long): Int?

    @Query("SELECT daily_cost, current_streak FROM habits WHERE habit_id = :habitId")
    suspend fun getCostAndStreak(habitId: Long): CostAndStreakResult?

    // Search functionality
    @Query("SELECT * FROM habits WHERE user_id = :userId AND (name LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%') ORDER BY created_at DESC")
    fun searchHabits(userId: Long, searchQuery: String): Flow<List<HabitEntity>>

    // Cleanup operations
    @Query("DELETE FROM habits WHERE user_id = :userId")
    suspend fun deleteAllHabitsForUser(userId: Long)

    @Query("DELETE FROM habits WHERE habit_id = :habitId")
    suspend fun deleteHabitById(habitId: Long)
}

// Data class for cost and streak query result
data class CostAndStreakResult(
    @ColumnInfo(name = "daily_cost") val dailyCost: Double?,
    @ColumnInfo(name = "current_streak") val currentStreak: Int
)