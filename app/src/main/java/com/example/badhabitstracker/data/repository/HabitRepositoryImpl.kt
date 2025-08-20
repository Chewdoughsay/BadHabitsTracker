package com.example.badhabitstracker.data.repository

import com.example.badhabitstracker.data.database.HabitDao
import com.example.badhabitstracker.data.database.HabitEntryDao
import com.example.badhabitstracker.data.database.entities.toDomain
import com.example.badhabitstracker.data.database.entities.toEntity
import com.example.badhabitstracker.domain.model.Habit
import com.example.badhabitstracker.domain.model.HabitEntry
import com.example.badhabitstracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class HabitRepositoryImpl(
    private val habitDao: HabitDao,
    private val habitEntryDao: HabitEntryDao
) : HabitRepository {

    companion object {
        private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

    // ============ HABIT CRUD OPERATIONS ============

    override suspend fun insertHabit(habit: Habit): Long {
        return habitDao.insertHabit(habit.toEntity())
    }

    override suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit.toEntity())
    }

    override suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit.toEntity())
    }

    override suspend fun getHabitById(id: Long): Habit? {
        return habitDao.getHabitById(id)?.toDomain()
    }

    // ============ HABIT QUERIES ============

    override fun getAllHabits(userId: Long): Flow<List<Habit>> {
        return habitDao.getAllHabits(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getActiveHabits(userId: Long): Flow<List<Habit>> {
        return habitDao.getActiveHabits(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCompletedHabits(userId: Long): Flow<List<Habit>> {
        return habitDao.getCompletedHabits(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getHabitsCount(userId: Long): Int {
        return habitDao.getHabitsCount(userId)
    }

    // ============ HABIT ENTRY OPERATIONS ============

    override suspend fun insertHabitEntry(entry: HabitEntry): Long {
        return habitEntryDao.insertHabitEntry(entry.toEntity())
    }

    override suspend fun updateHabitEntry(entry: HabitEntry) {
        habitEntryDao.updateHabitEntry(entry.toEntity())
    }

    override suspend fun deleteHabitEntry(entry: HabitEntry) {
        habitEntryDao.deleteHabitEntry(entry.toEntity())
    }

    // ============ HABIT ENTRY QUERIES ============

    override fun getEntriesForUser(userId: Long): Flow<List<HabitEntry>> {
        return habitEntryDao.getEntriesForUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getEntriesForHabit(habitId: Long): Flow<List<HabitEntry>> {
        return habitEntryDao.getEntriesForHabit(habitId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getEntryForDate(habitId: Long, date: String): HabitEntry? {
        return habitEntryDao.getEntryForDate(habitId, date)?.toDomain()
    }

    override suspend fun getLastEntryForHabit(habitId: Long): HabitEntry? {
        return habitEntryDao.getLastEntryForHabit(habitId)?.toDomain()
    }

    // ============ STATISTICS - BUSINESS LOGIC IN DOMAIN LAYER ============

    override suspend fun calculateCurrentStreak(habitId: Long): Int {
        val entries = habitEntryDao.getAllEntriesForStreakCalculation(habitId)
            .map { it.toDomain() }
            .sortedByDescending { it.date } // Most recent first

        if (entries.isEmpty()) return 0

        var streak = 0
        val calendar = Calendar.getInstance()

        // Start from today and work backwards
        calendar.time = Date()
        stripTime(calendar)
        val today = calendar.time

        // Check if there's an entry for today or yesterday (to allow for daily checking)
        val latestEntry = entries.first()
        val latestEntryCalendar = Calendar.getInstance()
        latestEntryCalendar.time = latestEntry.date
        stripTime(latestEntryCalendar)

        val daysDifference = ((today.time - latestEntryCalendar.time.time) / (1000 * 60 * 60 * 24)).toInt()

        // If latest entry is more than 1 day old, streak is broken
        if (daysDifference > 1) return 0

        // Count consecutive successful days from the most recent
        for (entry in entries) {
            if (entry.wasSuccessful) {
                streak++
            } else {
                break // Streak broken
            }
        }

        return streak
    }

    override suspend fun calculateLongestStreak(habitId: Long): Int {
        val entries = habitEntryDao.getAllEntriesForStreakCalculation(habitId)
            .map { it.toDomain() }
            .sortedBy { it.date } // Chronological order

        if (entries.isEmpty()) return 0

        var longestStreak = 0
        var currentStreak = 0

        for (entry in entries) {
            if (entry.wasSuccessful) {
                currentStreak++
                longestStreak = maxOf(longestStreak, currentStreak)
            } else {
                currentStreak = 0 // Reset streak on failure
            }
        }

        return longestStreak
    }

    override suspend fun getSuccessRate(habitId: Long): Double {
        val stats = habitEntryDao.getSuccessStats(habitId)
        return if (stats != null && stats.total > 0) {
            (stats.successful.toDouble() / stats.total.toDouble()) * 100.0
        } else {
            0.0
        }
    }

    override suspend fun getTotalMoneySaved(habitId: Long): Double {
        // Get habit's daily cost and current streak
        val costAndStreak = habitDao.getCostAndStreak(habitId)
        return if (costAndStreak?.dailyCost != null) {
            costAndStreak.dailyCost * costAndStreak.currentStreak
        } else {
            0.0
        }
    }

    // ============ HELPER FUNCTIONS ============

    /**
     * Strips time from calendar to compare dates only
     */
    private fun stripTime(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    /**
     * Formats date to string for database queries
     */
    private fun formatDateForQuery(date: Date): String {
        return dateFormatter.format(date)
    }

    /**
     * Checks if two dates are consecutive (day after day)
     */
    private fun areConsecutiveDays(laterDate: Date, earlierDate: Date): Boolean {
        val calendar = Calendar.getInstance()

        calendar.time = earlierDate
        calendar.add(Calendar.DAY_OF_YEAR, 1)

        val nextDay = calendar.time
        val laterCalendar = Calendar.getInstance()
        laterCalendar.time = laterDate

        stripTime(calendar)
        stripTime(laterCalendar)

        return calendar.timeInMillis == laterCalendar.timeInMillis
    }
}