package com.example.badhabitstracker.domain.usecase

import com.example.badhabitstracker.domain.model.UserSettings
import com.example.badhabitstracker.domain.repository.SharedPreferencesRepository
import com.example.badhabitstracker.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUserSettingsFlowUseCase(
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): Flow<UserSettings> {
        val userId = userRepository.getCurrentUserId()
            ?: throw IllegalStateException("No user logged in")

        return sharedPreferencesRepository.getUserSettingsFlow(userId)
    }
}