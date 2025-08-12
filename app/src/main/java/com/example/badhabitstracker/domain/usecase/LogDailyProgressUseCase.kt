package com.example.badhabitstracker.domain.usecase

import com.example.badhabitstracker.domain.model.HabitEntry
import com.example.badhabitstracker.domain.model.MoodLevel
import com.example.badhabitstracker.domain.repository.AchievementRepository
import com.example.badhabitstracker.domain.repository.HabitRepository
import com.example.badhabitstracker.domain.repository.UserRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LogProgressParams(
    val habitId: Long,
    val wasSuccessful: Boolean,
    val notes: String? = null,
    val moodLevel: MoodLevel? = null,
    val date: Date = Date()
)

class LogDailyProgressUseCase(
    private val habitRepository: HabitRepository,
    private val achievementRepository: AchievementRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUser<LogProgressParams, Unit>(userRepository) {

    override suspend fun execute(parameters: LogProgressParams, userId: Long) {

        //validare
        val habit = habitRepository.getHabitById(parameters.habitId)
            ?: throw IllegalArgumentException("Habit not found")

        require(habit.isActive) { "Cannot log progress for inactive habit" }
        require(habit.userId == userId) { "Cannot log progress for habit that doesn't belong to current user" }

        // verificam daca deja exista un entry pt ziua asta
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(parameters.date)
        val existingEntry = habitRepository.getEntryForDate(parameters.habitId, dateString)

        val entry = HabitEntry(
            id = existingEntry?.id ?: 0,
            habitId = parameters.habitId,
            userId = userId,
            date = parameters.date,
            wasSuccessful = parameters.wasSuccessful,
            notes = parameters.notes?.trim(),
            moodLevel = parameters.moodLevel
        )

        // actualizam sau inseram entryul
        if (existingEntry != null) {
            habitRepository.updateHabitEntry(entry)
        } else {
            habitRepository.insertHabitEntry(entry)
        }

        //recalculam streakurile
        val newCurrentStreak = habitRepository.calculateCurrentStreak(parameters.habitId)
        val newLongestStreak = habitRepository.calculateLongestStreak(parameters.habitId)

        // update habit
        val updatedHabit = habit.copy(
            currentStreak = newCurrentStreak,
            longestStreak = maxOf(newLongestStreak, habit.longestStreak),
            updatedAt = Date()
        )
        habitRepository.updateHabit(updatedHabit)


        // check for new achievement
        if (parameters.wasSuccessful) {
            achievementRepository.checkAndUnlockAchievements(
                habitId = parameters.habitId,
                userId = userId,
                currentStreak = newCurrentStreak,
                isFirstHabit = false
            )
        }
    }
}