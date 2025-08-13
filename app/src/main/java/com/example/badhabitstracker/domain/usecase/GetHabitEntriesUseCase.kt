package com.example.badhabitstracker.domain.usecase

import com.example.badhabitstracker.domain.model.HabitEntry
import com.example.badhabitstracker.domain.repository.HabitRepository
import com.example.badhabitstracker.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

/**
 *get entries for habit (for RecyclerView)
 */
class GetHabitEntriesUseCase(
    private val habitRepository: HabitRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUser<Long, Flow<List<HabitEntry>>>(userRepository) {

    override suspend fun execute(parameters: Long, userId: Long): Flow<List<HabitEntry>> {
        // Security check: verify habit belongs to user
        val habit = habitRepository.getHabitById(parameters)
        require(habit?.userId == userId) { "Habit does not belong to current user" }

        return habitRepository.getEntriesForHabit(parameters)
    }
}