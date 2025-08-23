package com.example.badhabitstracker.A_domain.usecase

import com.example.badhabitstracker.A_domain.model.UserWithHabits
import com.example.badhabitstracker.A_domain.repository.HabitRepository
import com.example.badhabitstracker.A_domain.repository.UserRepository
import kotlinx.coroutines.flow.first

/**
 * user cu toate habiturile lui
 */
class GetUserWithHabitsUseCase(
    private val userRepository: UserRepository,
    private val habitRepository: HabitRepository
) : BaseUseCaseWithCurrentUserNoParams<UserWithHabits>(userRepository) {

    override suspend fun execute(userId: Long): UserWithHabits {
        // get user
        val user = userRepository.getUserById(userId)
            ?: throw IllegalStateException("User not found")

        // get habits for user
        val habits = habitRepository.getAllHabits(userId).first()

        return UserWithHabits(
            user = user,
            habits = habits
        )
    }
}