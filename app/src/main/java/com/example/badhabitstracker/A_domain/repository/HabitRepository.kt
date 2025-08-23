package com.example.badhabitstracker.A_domain.repository

import com.example.badhabitstracker.A_domain.model.Habit
import com.example.badhabitstracker.A_domain.model.HabitEntry
import kotlinx.coroutines.flow.Flow

interface HabitRepository {

    // operatii CRUD
    suspend fun insertHabit(habit: Habit): Long
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habit: Habit)
    suspend fun getHabitById(id: Long): Habit?

    // queryuri pt Habit
    fun getAllHabits(userId: Long): Flow<List<Habit>>
    fun getActiveHabits(userId: Long): Flow<List<Habit>>
    fun getCompletedHabits(userId: Long): Flow<List<Habit>>
    suspend fun getHabitsCount(userId: Long): Int

    // operatii pt HabitEntry
    suspend fun insertHabitEntry(entry: HabitEntry): Long
    suspend fun updateHabitEntry(entry: HabitEntry)
    suspend fun deleteHabitEntry(entry: HabitEntry)

    // queryuri pt HabitEntry
    fun getEntriesForUser(userId: Long): Flow<List<HabitEntry>>
    fun getEntriesForHabit(habitId: Long): Flow<List<HabitEntry>>
    suspend fun getEntryForDate(habitId: Long, date: String): HabitEntry?
    suspend fun getLastEntryForHabit(habitId: Long): HabitEntry?

    // statistici
    suspend fun calculateCurrentStreak(habitId: Long): Int
    suspend fun calculateLongestStreak(habitId: Long): Int
    suspend fun getSuccessRate(habitId: Long): Double
    suspend fun getTotalMoneySaved(habitId: Long): Double
}