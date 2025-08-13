package com.example.badhabitstracker.domain.usecase

import com.example.badhabitstracker.domain.model.UserSettings
import com.example.badhabitstracker.domain.repository.SharedPreferencesRepository
import com.example.badhabitstracker.domain.repository.UserRepository

class UpdateUserSettingsUseCase(
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val userRepository: UserRepository
) : BaseUseCaseWithCurrentUser<UserSettings, Unit>(userRepository) {

    override suspend fun execute(parameters: UserSettings, userId: Long) {

        //validation
        require(parameters.dailyReminderTime.matches(Regex("\\d{2}:\\d{2}"))) {
            "Invalid time format. Use HH:mm"
        }

        // save to SharedPreferences (requirement)
        sharedPreferencesRepository.saveUserSettings(userId, parameters)

        // update user in database
        val currentUser = userRepository.getUserById(userId)
            ?: throw IllegalStateException("User not found")

        val updatedUser = currentUser.copy(settings = parameters)
        userRepository.updateUser(updatedUser)
    }
}