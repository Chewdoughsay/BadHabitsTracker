package com.example.badhabitstracker.domain.usecase.authentification

import com.example.badhabitstracker.domain.repository.UserRepository
import com.example.badhabitstracker.domain.usecase.BaseUseCaseNoParams

class IsUserLoggedInUseCase(
    private val userRepository: UserRepository
) : BaseUseCaseNoParams<Boolean>() {

    override suspend fun execute(): Boolean {
        return userRepository.isUserLoggedIn()
    }
}