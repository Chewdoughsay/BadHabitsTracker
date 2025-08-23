package com.example.badhabitstracker.A_domain.usecase

import com.example.badhabitstracker.A_domain.model.UserSettings
import com.example.badhabitstracker.A_domain.repository.SharedPreferencesRepository
import com.example.badhabitstracker.A_domain.repository.UserRepository

class GetUserSettingsUseCase(
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUserNoParams<UserSettings>(userRepository) {

    override suspend fun execute(userId: Long): UserSettings {
        return sharedPreferencesRepository.getUserSettings(userId)
    }
}