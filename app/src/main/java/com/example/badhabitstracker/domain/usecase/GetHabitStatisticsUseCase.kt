package com.example.badhabitstracker.domain.usecase

import com.example.badhabitstracker.domain.model.Habit
import com.example.badhabitstracker.domain.model.HabitEntry
import com.example.badhabitstracker.domain.repository.HabitRepository
import com.example.badhabitstracker.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Date
import java.util.Calendar

data class HabitStatistics(
    val habit: Habit,
    val currentStreak: Int,
    val longestStreak: Int,
    val successRate: Double,
    val totalMoneySaved: Double,
    val totalDays: Int,
    val successfulDays: Int,
    val failedDays: Int,
    val progressPercentage: Int?,
    val daysRemaining: Int?,
    val recentEntries: List<HabitEntry>
)


class GetHabitStatisticsUseCase(
    private val habitRepository: HabitRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUser<Long, HabitStatistics>(userRepository) {

    override suspend fun execute(parameters: Long, userId: Long): HabitStatistics {

        val habit = habitRepository.getHabitById(parameters)
            ?: throw IllegalArgumentException("Habit not found")

        require(habit.userId == userId) { "Habit does not belong to current user" }

        val entries = habitRepository.getEntriesForHabit(parameters).first()

        val currentStreak = habitRepository.calculateCurrentStreak(parameters)
        val longestStreak = habitRepository.calculateLongestStreak(parameters)
        val successRate = habitRepository.getSuccessRate(parameters)
        val totalMoneySaved = habitRepository.getTotalMoneySaved(parameters)

        val successfulDays = entries.count { it.wasSuccessful }
        val failedDays = entries.count { !it.wasSuccessful }
        val totalDays = entries.size

        //get recent entries (last 30 days)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val thirtyDaysAgo = calendar.time

        val recentEntries = entries
            .filter { it.date.after(thirtyDaysAgo) }
            .sortedByDescending { it.date }
            .take(30)

        return HabitStatistics(
            habit = habit,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            successRate = successRate,
            totalMoneySaved = totalMoneySaved,
            totalDays = totalDays,
            successfulDays = successfulDays,
            failedDays = failedDays,
            progressPercentage = habit.getProgressPercentage(),
            daysRemaining = habit.getDaysRemaining(),
            recentEntries = recentEntries
        )
    }
}