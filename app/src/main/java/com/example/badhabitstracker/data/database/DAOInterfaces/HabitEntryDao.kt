package com.example.badhabitstracker.data.database

import androidx.room.*
import com.example.badhabitstracker.data.database.entities.HabitEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitEntryDao {

    // Basic CRUD operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitEntry(entry: HabitEntryEntity): Long

    @Update
    suspend fun updateHabitEntry(entry: HabitEntryEntity)

    @Delete
    suspend fun deleteHabitEntry(entry: HabitEntryEntity)

    // Query operations
    @Query("SELECT * FROM habit_entries WHERE entry_id = :entryId")
    suspend fun getEntryById(entryId: Long): HabitEntryEntity?

    @Query("SELECT * FROM habit_entries WHERE habit_id = :habitId ORDER BY date DESC")
    fun getEntriesForHabit(habitId: Long): Flow<List<HabitEntryEntity>>

    @Query("SELECT * FROM habit_entries WHERE user_id = :userId ORDER BY date DESC")
    fun getEntriesForUser(userId: Long): Flow<List<HabitEntryEntity>>

    // Date-specific queries (for checking if entry exists for a day)
    @Query("SELECT * FROM habit_entries WHERE habit_id = :habitId AND date(date/1000, 'unixepoch') = :dateString LIMIT 1")
    suspend fun getEntryForDate(habitId: Long, dateString: String): HabitEntryEntity?

    @Query("SELECT * FROM habit_entries WHERE habit_id = :habitId ORDER BY date DESC LIMIT 1")
    suspend fun getLastEntryForHabit(habitId: Long): HabitEntryEntity?

    // Statistics queries for streak calculation
    @Query("""
        SELECT * FROM habit_entries 
        WHERE habit_id = :habitId 
        ORDER BY date DESC 
        LIMIT :limit
    """)
    suspend fun getRecentEntries(habitId: Long, limit: Int): List<HabitEntryEntity>

    @Query("""
        SELECT * FROM habit_entries 
        WHERE habit_id = :habitId 
        ORDER BY date ASC
    """)
    suspend fun getAllEntriesForStreakCalculation(habitId: Long): List<HabitEntryEntity>

    // Success rate calculations
    @Query("SELECT COUNT(*) FROM habit_entries WHERE habit_id = :habitId")
    suspend fun getTotalEntriesCount(habitId: Long): Int

    @Query("SELECT COUNT(*) FROM habit_entries WHERE habit_id = :habitId AND was_successful = 1")
    suspend fun getSuccessfulEntriesCount(habitId: Long): Int

    @Query("""
        SELECT 
            COUNT(*) as total,
            SUM(CASE WHEN was_successful = 1 THEN 1 ELSE 0 END) as successful
        FROM habit_entries 
        WHERE habit_id = :habitId
    """)
    suspend fun getSuccessStats(habitId: Long): SuccessStatsResult?

    // Date range queries
    @Query("""
        SELECT * FROM habit_entries 
        WHERE habit_id = :habitId 
        AND date >= :startDate 
        AND date <= :endDate 
        ORDER BY date DESC
    """)
    fun getEntriesInDateRange(habitId: Long, startDate: Long, endDate: Long): Flow<List<HabitEntryEntity>>

    // Recent entries for dashboard (last 30 days)
    @Query("""
        SELECT * FROM habit_entries 
        WHERE habit_id = :habitId 
        AND date >= :thirtyDaysAgo 
        ORDER BY date DESC 
        LIMIT 30
    """)
    suspend fun getRecentEntriesForStats(habitId: Long, thirtyDaysAgo: Long): List<HabitEntryEntity>

    // Mood analysis queries
    @Query("""
        SELECT mood, COUNT(*) as count 
        FROM habit_entries 
        WHERE habit_id = :habitId AND mood IS NOT NULL 
        GROUP BY mood
    """)
    suspend fun getMoodDistribution(habitId: Long): List<MoodDistributionResult>

    // Calendar view queries (for showing progress in calendar)
    @Query("""
        SELECT date, was_successful 
        FROM habit_entries 
        WHERE habit_id = :habitId 
        AND date >= :startOfMonth 
        AND date <= :endOfMonth 
        ORDER BY date ASC
    """)
    suspend fun getMonthlyProgress(habitId: Long, startOfMonth: Long, endOfMonth: Long): List<DailyProgressResult>

    // Cleanup operations
    @Query("DELETE FROM habit_entries WHERE habit_id = :habitId")
    suspend fun deleteAllEntriesForHabit(habitId: Long)

    @Query("DELETE FROM habit_entries WHERE user_id = :userId")
    suspend fun deleteAllEntriesForUser(userId: Long)

    @Query("DELETE FROM habit_entries WHERE entry_id = :entryId")
    suspend fun deleteEntryById(entryId: Long)
}

// Data classes for query results
data class SuccessStatsResult(
    val total: Int,
    val successful: Int
)

data class MoodDistributionResult(
    val mood: String,
    val count: Int
)

data class DailyProgressResult(
    val date: Long,
    @ColumnInfo(name = "was_successful") val wasSuccessful: Boolean
)