package com.example.badhabitstracker.A_domain.usecase.authentification

import com.example.badhabitstracker.A_domain.repository.UserRepository
import com.example.badhabitstracker.A_domain.usecase.BaseUseCaseNoParams

class LogoutUserUseCase(
    private val userRepository: UserRepository
) : BaseUseCaseNoParams<Unit>() {

    override suspend fun execute() {
        // UserRepository handles all session cleanup internally
        userRepository.logoutUser()
    }
}