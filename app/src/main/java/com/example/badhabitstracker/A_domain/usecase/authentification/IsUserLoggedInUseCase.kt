package com.example.badhabitstracker.A_domain.usecase.authentification

import com.example.badhabitstracker.A_domain.repository.UserRepository
import com.example.badhabitstracker.A_domain.usecase.BaseUseCaseNoParams

class IsUserLoggedInUseCase(
    private val userRepository: UserRepository
) : BaseUseCaseNoParams<Boolean>() {

    override suspend fun execute(): Boolean {
        return userRepository.isUserLoggedIn()
    }
}