package com.example.badhabitstracker.A_domain.usecase.authentification

import com.example.badhabitstracker.A_domain.model.User
import com.example.badhabitstracker.A_domain.repository.UserRepository
import com.example.badhabitstracker.A_domain.usecase.BaseUseCaseNoParams

class GetCurrentUserUseCase(
    private val userRepository: UserRepository
) : BaseUseCaseNoParams<User?>() {

    override suspend fun execute(): User? {
        return userRepository.getCurrentUser()
    }
}