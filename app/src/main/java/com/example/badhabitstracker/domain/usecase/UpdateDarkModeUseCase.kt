package com.example.badhabitstracker.domain.usecase

import com.example.badhabitstracker.domain.repository.SharedPreferencesRepository
import com.example.badhabitstracker.domain.repository.UserRepository

class UpdateDarkModeUseCase(
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUser<Boolean, Unit>(userRepository) {

    override suspend fun execute(parameters: Boolean, userId: Long) {
        sharedPreferencesRepository.setDarkModeEnabled(userId, parameters)
    }
}