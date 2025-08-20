package com.example.badhabitstracker.domain.usecase.authentification

import com.example.badhabitstracker.domain.repository.UserRepository
import com.example.badhabitstracker.domain.usecase.BaseUseCaseNoParams

class LogoutUserUseCase(
    private val userRepository: UserRepository
) : BaseUseCaseNoParams<Unit>() {

    override suspend fun execute() {
        // UserRepository handles all session cleanup internally
        userRepository.logoutUser()
    }
}