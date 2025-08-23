package com.example.badhabitstracker.A_domain.usecase

import com.example.badhabitstracker.A_domain.repository.SharedPreferencesRepository
import com.example.badhabitstracker.A_domain.repository.UserRepository

class UpdateDarkModeUseCase(
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUser<Boolean, Unit>(userRepository) {

    override suspend fun execute(parameters: Boolean, userId: Long) {
        sharedPreferencesRepository.setDarkModeEnabled(userId, parameters)
    }
}