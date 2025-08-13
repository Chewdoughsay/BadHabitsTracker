package com.example.badhabitstracker.domain.usecase

import com.example.badhabitstracker.domain.repository.SharedPreferencesRepository
import com.example.badhabitstracker.domain.repository.UserRepository

class UpdateReminderTimeUseCase(
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUser<String, Unit>(userRepository) {

    override suspend fun execute(parameters: String, userId: Long) {
        require(parameters.matches(Regex("\\d{2}:\\d{2}"))) {
            "Invalid time format. Use HH:mm"
        }
        sharedPreferencesRepository.setDailyReminderTime(userId, parameters)
    }
}