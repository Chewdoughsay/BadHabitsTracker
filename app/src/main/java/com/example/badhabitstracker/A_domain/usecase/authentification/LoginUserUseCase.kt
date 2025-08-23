package com.example.badhabitstracker.A_domain.usecase.authentification

import com.example.badhabitstracker.A_domain.model.User
import com.example.badhabitstracker.A_domain.repository.UserRepository
import com.example.badhabitstracker.A_domain.usecase.BaseUseCase

data class LoginParams(
    val email: String,
    val password: String
)

class LoginUserUseCase(
    private val userRepository: UserRepository
) : BaseUseCase<LoginParams, User>() {

    override suspend fun execute(parameters: LoginParams): User {
        // Input validation (Use Case responsibility)
        require(parameters.email.isNotBlank()) { "Email cannot be empty" }
        require(parameters.password.isNotBlank()) { "Password cannot be empty" }

        // Login user (Repository handles data validation like "invalid credentials")
        val result = userRepository.loginUser(
            email = parameters.email.trim().lowercase(),
            password = parameters.password
        )

        return result.getOrElse { exception ->
            throw exception
        }.also { user ->
            // UserRepository owns ALL session logic internally
            userRepository.saveUserSession(user)
        }
    }
}