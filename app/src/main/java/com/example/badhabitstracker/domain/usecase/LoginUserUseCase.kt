package com.example.badhabitstracker.domain.usecase

import com.example.badhabitstracker.domain.model.User
import com.example.badhabitstracker.domain.repository.SharedPreferencesRepository
import com.example.badhabitstracker.domain.repository.UserRepository

data class LoginParams(
    val email: String,
    val password: String
)

class LoginUserUseCase(
    private val userRepository: UserRepository,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : BaseUseCase<LoginParams, User>() {

    override suspend fun execute(parameters: LoginParams): User {

        require(parameters.email.isNotBlank()) { "Email cannot be empty" }
        require(parameters.password.isNotBlank()) { "Password cannot be empty" }

        //login user
        val result = userRepository.loginUser(
            email = parameters.email.trim().lowercase(),
            password = parameters.password
        )

        return result.getOrElse { exception ->
            throw exception
        }.also { user ->
            userRepository.saveUserSession(user)
            sharedPreferencesRepository.saveUserId(user.id)
        }
    }
}

/**
 * verifica daca e deja logat
 */
class IsUserLoggedInUseCase(
    private val userRepository: UserRepository
) : BaseUseCaseNoParams<Boolean>() {

    override suspend fun execute(): Boolean {
        return userRepository.isUserLoggedIn()
    }
}

class GetCurrentUserUseCase(
    private val userRepository: UserRepository
) : BaseUseCaseNoParams<User?>() {

    override suspend fun execute(): User? {
        return userRepository.getCurrentUser()
    }
}

class LogoutUserUseCase(
    private val userRepository: UserRepository,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : BaseUseCaseNoParams<Unit>() {

    override suspend fun execute() {
        // Get current user ID before clearing session
        val userId = userRepository.getCurrentUserId()

        // Clear user session
        userRepository.logoutUser()

        // Clear shared preferences data
        sharedPreferencesRepository.clearUserId()

        userId?.let { id ->
            sharedPreferencesRepository.clearUserData(id)
        }
    }
}
