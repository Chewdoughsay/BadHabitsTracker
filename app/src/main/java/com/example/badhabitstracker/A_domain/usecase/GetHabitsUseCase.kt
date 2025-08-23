package com.example.badhabitstracker.A_domain.usecase

import com.example.badhabitstracker.A_domain.model.Habit
import com.example.badhabitstracker.A_domain.repository.HabitRepository
import com.example.badhabitstracker.A_domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

enum class HabitFilter {
    ALL,
    ACTIVE_ONLY,
    COMPLETED_ONLY
}

data class GetHabitsParams(
    val filter: HabitFilter = HabitFilter.ACTIVE_ONLY
)

/**
 * flow cu lista de Habits cu filtru
 */
class GetHabitsUseCase(
    private val habitRepository: HabitRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUser<GetHabitsParams, Flow<List<Habit>>>(userRepository) {

    override suspend fun execute(parameters: GetHabitsParams, userId: Long): Flow<List<Habit>> {
        return when (parameters.filter) {
            HabitFilter.ALL -> habitRepository.getAllHabits(userId)
            HabitFilter.ACTIVE_ONLY -> habitRepository.getActiveHabits(userId)
            HabitFilter.COMPLETED_ONLY -> habitRepository.getCompletedHabits(userId)
        }
    }
}

/**
 * use case pentru a obtine un Habit dupa id
 */
class GetHabitByIdUseCase(
    private val habitRepository: HabitRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUser<Long, Habit?>(userRepository) {

    override suspend fun execute(parameters: Long, userId: Long): Habit? {
        val habit = habitRepository.getHabitById(parameters)

        // ne asiguram ca habitul apartine userului curent
        return if (habit?.userId == userId) habit else null
    }
}