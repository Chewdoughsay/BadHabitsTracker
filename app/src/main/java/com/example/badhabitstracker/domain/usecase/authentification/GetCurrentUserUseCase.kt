package com.example.badhabitstracker.domain.usecase.authentification

import com.example.badhabitstracker.domain.model.User
import com.example.badhabitstracker.domain.repository.UserRepository
import com.example.badhabitstracker.domain.usecase.BaseUseCaseNoParams

class GetCurrentUserUseCase(
    private val userRepository: UserRepository
) : BaseUseCaseNoParams<User?>() {

    override suspend fun execute(): User? {
        return userRepository.getCurrentUser()
    }
}