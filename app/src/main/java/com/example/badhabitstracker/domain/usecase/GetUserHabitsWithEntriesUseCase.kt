package com.example.badhabitstracker.domain.usecase

import com.example.badhabitstracker.domain.model.HabitWithEntries
import com.example.badhabitstracker.domain.repository.HabitRepository
import com.example.badhabitstracker.domain.repository.UserRepository
import kotlinx.coroutines.flow.first

/**
 * GetUserHabitsWithEntriesUseCase.kt returneaza toate habiturile active ale userului logat impreuna cu entry urile acestora
 */
class GetUserHabitsWithEntriesUseCase(
    private val habitRepository: HabitRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUserNoParams<List<HabitWithEntries>>(userRepository) {

    override suspend fun execute(userId: Long): List<HabitWithEntries> {
        // ia toate habiturile active ale userului
        val habits = habitRepository.getActiveHabits(userId).first()

        // For each habit, get its entries and combine
        return habits.map { habit ->
            val entries = habitRepository.getEntriesForHabit(habit.id).first()
            HabitWithEntries(
                habit = habit,
                entries = entries
            )
        }
    }
}