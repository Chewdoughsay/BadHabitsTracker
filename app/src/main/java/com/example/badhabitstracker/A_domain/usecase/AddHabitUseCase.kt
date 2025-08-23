package com.example.badhabitstracker.A_domain.usecase

import com.example.badhabitstracker.A_domain.model.Habit
import com.example.badhabitstracker.A_domain.model.HabitCategory
import com.example.badhabitstracker.A_domain.repository.AchievementRepository
import com.example.badhabitstracker.A_domain.repository.HabitRepository
import com.example.badhabitstracker.A_domain.repository.UserRepository
import java.util.Date

data class AddHabitParams(
    val name: String,
    val description: String,
    val category: HabitCategory,
    val targetDays: Int? = null,
    val dailyCost: Double? = null
)

class AddHabitUseCase(
    private val habitRepository: HabitRepository,
    private val achievementRepository: AchievementRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUser<AddHabitParams, Long>(userRepository) {

    override suspend fun execute(parameters: AddHabitParams, userId: Long): Long {

        //validari parametrii
        require(parameters.name.isNotBlank()) { "Habit name cannot be empty" }
        require(parameters.description.isNotBlank()) { "Habit description cannot be empty" }
        require(parameters.targetDays == null || parameters.targetDays > 0) { "Target days must be positive" }
        require(parameters.dailyCost == null || parameters.dailyCost >= 0) { "Daily cost cannot be negative" }

        // cream un habit
        val habit = Habit(
            userId = userId,
            name = parameters.name.trim(),
            description = parameters.description.trim(),
            category = parameters.category,
            startDate = Date(),
            targetDays = parameters.targetDays,
            dailyCost = parameters.dailyCost,
            currentStreak = 0,
            longestStreak = 0,
            isActive = true
        )

        // salvam habitu in baza de date
        val habitId = habitRepository.insertHabit(habit)

        val totalHabits = habitRepository.getHabitsCount(userId)
        val isFirstHabit = totalHabits == 1

        // verificam si deblocam achivementurile (primu)
        achievementRepository.checkAndUnlockAchievements(
            habitId = habitId,
            userId = userId,
            currentStreak = 0,
            isFirstHabit = isFirstHabit
        )

        return habitId
    }
}