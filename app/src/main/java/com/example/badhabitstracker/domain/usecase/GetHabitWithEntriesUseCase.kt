package com.example.badhabitstracker.domain.usecase

import com.example.badhabitstracker.domain.model.HabitWithEntries
import com.example.badhabitstracker.domain.repository.HabitRepository
import com.example.badhabitstracker.domain.repository.UserRepository
import kotlinx.coroutines.flow.first

/**
 * GetHabitWithEntriesUseCase.kt returneaza un habit al userului logat impreuna cu entry urile lui
 */
class GetHabitWithEntriesUseCase(
    private val habitRepository: HabitRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUser<Long, HabitWithEntries?>(userRepository) {

    override suspend fun execute(parameters: Long, userId: Long): HabitWithEntries? {
        val habit = habitRepository.getHabitById(parameters)
            ?: return null

        //security check
        if (habit.userId != userId) {
            throw IllegalAccessException("Habit does not belong to current user")
        }

        //ia prima lista din db emisa de flow
        val entries = habitRepository.getEntriesForHabit(parameters).first()

        //HabitWithEntries
        return HabitWithEntries(
            habit = habit,
            entries = entries
        )
    }
}