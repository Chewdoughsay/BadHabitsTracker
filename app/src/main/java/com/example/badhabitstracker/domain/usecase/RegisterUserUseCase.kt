package com.example.badhabitstracker.domain.usecase

import com.example.badhabitstracker.domain.model.User
import com.example.badhabitstracker.domain.repository.UserRepository
import com.example.badhabitstracker.domain.repository.SharedPreferencesRepository

data class RegisterParams(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val name: String
)

class RegisterUserUseCase(
    private val userRepository: UserRepository,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : BaseUseCase<RegisterParams, User>() {

    companion object {
        private val EMAIL_REGEX = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        private const val MIN_PASSWORD_LENGTH = 6
    }

    override suspend fun execute(parameters: RegisterParams): User {

        // validare
        require(parameters.email.isNotBlank()) { "Email cannot be empty" }
        require(parameters.email.matches(EMAIL_REGEX)) { "Invalid email format" }
        require(parameters.password.isNotBlank()) { "Password cannot be empty" }
        require(parameters.password.length >= MIN_PASSWORD_LENGTH) {
            "Password must be at least $MIN_PASSWORD_LENGTH characters"
        }
        require(parameters.password == parameters.confirmPassword) {
            "Passwords do not match"
        }
        require(parameters.name.isNotBlank()) { "Name cannot be empty" }

        // register user
        val result = userRepository.registerUser(
            email = parameters.email.trim().lowercase(),
            password = parameters.password,
            name = parameters.name.trim()
        )

        // return user or throw exception, "also" permite sa facem actiuni secundare pe obiectul obtinut
        return result.getOrElse { exception ->
            throw exception
        }.also { user ->
            // save user session and ID
            userRepository.saveUserSession(user)
            sharedPreferencesRepository.saveUserId(user.id)
        }
    }
}