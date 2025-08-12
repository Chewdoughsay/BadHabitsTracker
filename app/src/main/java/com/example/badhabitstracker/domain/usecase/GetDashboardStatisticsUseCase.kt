package com.example.badhabitstracker.domain.usecase

import com.example.badhabitstracker.domain.repository.AchievementRepository
import com.example.badhabitstracker.domain.repository.HabitRepository
import com.example.badhabitstracker.domain.repository.UserRepository
import kotlinx.coroutines.flow.first

data class DashboardStatistics(
    val totalActiveHabits: Int,
    val totalCompletedHabits: Int,
    val totalDaysTracked: Int,
    val totalMoneySaved: Double,
    val averageSuccessRate: Double,
    val longestStreakEver: Int,
    val unviewedAchievements: Int
)

/**
 * statistici pentru dashboard
 */
class GetDashboardStatisticsUseCase(
    private val habitRepository: HabitRepository,
    private val achievementRepository: AchievementRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUserNoParams<DashboardStatistics>(userRepository) {

    override suspend fun execute(userId: Long): DashboardStatistics {
        // habits
        val allHabits = habitRepository.getAllHabits(userId).first()
        val activeHabits = allHabits.filter { it.isActive }
        val completedHabits = allHabits.filter { !it.isActive }

        // calculate comprehensive statistics
        var totalDaysTracked = 0
        var totalMoneySaved = 0.0
        var totalSuccessfulDays = 0
        var longestStreakEver = 0

        for (habit in allHabits) {
            val entries = habitRepository.getEntriesForHabit(habit.id).first()
            totalDaysTracked += entries.size
            totalSuccessfulDays += entries.count { it.wasSuccessful }
            totalMoneySaved += habit.getMoneySaved()

            if (habit.longestStreak > longestStreakEver) {
                longestStreakEver = habit.longestStreak
            }
        }

        val averageSuccessRate = if (totalDaysTracked > 0) {
            (totalSuccessfulDays.toDouble() / totalDaysTracked) * 100
        } else 0.0

        val unviewedAchievements = achievementRepository.getUnviewedAchievementsCount(userId)

        return DashboardStatistics(
            totalActiveHabits = activeHabits.size,
            totalCompletedHabits = completedHabits.size,
            totalDaysTracked = totalDaysTracked,
            totalMoneySaved = totalMoneySaved,
            averageSuccessRate = averageSuccessRate,
            longestStreakEver = longestStreakEver,
            unviewedAchievements = unviewedAchievements
        )
    }
}