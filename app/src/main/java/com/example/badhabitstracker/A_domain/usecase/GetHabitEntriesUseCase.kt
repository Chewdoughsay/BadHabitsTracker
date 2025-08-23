package com.example.badhabitstracker.A_domain.usecase

import com.example.badhabitstracker.A_domain.model.HabitEntry
import com.example.badhabitstracker.A_domain.repository.HabitRepository
import com.example.badhabitstracker.A_domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

/**
 *get entries for habit (for RecyclerView)
 */
class GetHabitEntriesUseCase(
    private val habitRepository: HabitRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUser<Long, Flow<List<HabitEntry>>>(userRepository) {

    override suspend fun execute(parameters: Long, userId: Long): Flow<List<HabitEntry>> {
        val habit = habitRepository.getHabitById(parameters)
        require(habit?.userId == userId) { "Habit does not belong to current user" }

        return habitRepository.getEntriesForHabit(parameters)
    }
}