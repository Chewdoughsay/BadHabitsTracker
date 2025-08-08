package com.example.badhabitstracker.domain.repository

import com.example.badhabitstracker.domain.model.Habit
import com.example.badhabitstracker.domain.model.HabitEntry
import kotlinx.coroutines.flow.Flow

interface HabitRepository {

    // operatii CRUD
    suspend fun insertHabit(habit: Habit): Long
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habit: Habit)
    suspend fun getHabitById(id: Long): Habit?

    // queryuri pt Habit
    fun getAllHabits(): Flow<List<Habit>>
    fun getActiveHabits(): Flow<List<Habit>>
    fun getCompletedHabits(): Flow<List<Habit>>
    suspend fun getHabitsCount(): Int

    // operatii pt HabitEntry
    suspend fun insertHabitEntry(entry: HabitEntry): Long
    suspend fun updateHabitEntry(entry: HabitEntry)
    suspend fun deleteHabitEntry(entry: HabitEntry)

    // queryuri pt HabitEntry
    fun getEntriesForHabit(habitId: Long): Flow<List<HabitEntry>>
    suspend fun getEntryForDate(habitId: Long, date: String): HabitEntry?
    suspend fun getLastEntryForHabit(habitId: Long): HabitEntry?

    // statistici
    suspend fun calculateCurrentStreak(habitId: Long): Int
    suspend fun calculateLongestStreak(habitId: Long): Int
    suspend fun getSuccessRate(habitId: Long): Double
    suspend fun getTotalMoneySaved(habitId: Long): Double
}