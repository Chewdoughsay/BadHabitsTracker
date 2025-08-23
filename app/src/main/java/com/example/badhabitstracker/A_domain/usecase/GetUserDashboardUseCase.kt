package com.example.badhabitstracker.A_domain.usecase

import com.example.badhabitstracker.A_domain.model.UserDashboard
import com.example.badhabitstracker.A_domain.repository.AchievementRepository
import com.example.badhabitstracker.A_domain.repository.HabitRepository
import com.example.badhabitstracker.A_domain.repository.UserRepository
import kotlinx.coroutines.flow.first

class GetUserDashboardUseCase(
    private val userRepository: UserRepository,
    private val habitRepository: HabitRepository,
    private val achievementRepository: AchievementRepository
) : BaseUseCaseWithCurrentUserNoParams<UserDashboard>(userRepository) {

    override suspend fun execute(userId: Long): UserDashboard {
        // date despre user
        val user = userRepository.getUserById(userId)
            ?: throw IllegalStateException("User not found")

        // habiturile active ale userului
        val activeHabits = habitRepository.getActiveHabits(userId).first()

        // ultimele 5 achievements
        val recentAchievements = achievementRepository.getRecentAchievements(userId, limit = 5)

        // calculeaza statistici
        var totalSuccessfulDays = 0
        var totalMoneySaved = 0.0

        for (habit in activeHabits) {
            val entries = habitRepository.getEntriesForHabit(habit.id).first()
            totalSuccessfulDays += entries.count { it.wasSuccessful }
            totalMoneySaved += habit.getMoneySaved()
        }

        return UserDashboard(
            user = user,
            activeHabits = activeHabits,
            recentAchievements = recentAchievements,
            totalSuccessfulDays = totalSuccessfulDays,
            totalMoneySaved = totalMoneySaved
        )
    }
}