package com.example.badhabitstracker.A_domain.usecase

import com.example.badhabitstracker.A_domain.repository.UserRepository

/**
 * clasa de baza pt use caseuri care executa suspend functions
 */
abstract class BaseUseCase<in P, out R> {

    suspend operator fun invoke(parameters : P): Result<R> {
        return try {
            Result.success(execute(parameters))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // pt java, metoda poate arunca RuntimeException
    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): R
}

/**
 * acelasi lucru numa ca fara parametri
 */
abstract class BaseUseCaseNoParams<R> {

    suspend operator fun invoke(): Result<R> {
        return try {
            Result.success(execute())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(): R
}


/**
 * clasa de baza pt use case care necesita userul curent
 */
abstract class BaseUseCaseWithCurrentUser<in P, R>(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(parameters: P): Result<R> {
        return try {
            val userId = userRepository.getCurrentUserId()
                ?: throw IllegalStateException("No user logged in")

            Result.success(execute(parameters, userId))
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P, userId: Long): R
}

/**
 * acelasi lucru numa ca fara parametri
 */
abstract class BaseUseCaseWithCurrentUserNoParams<R>(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): Result<R> {
        return try {
            val userId = userRepository.getCurrentUserId()
                ?: throw IllegalStateException("No user logged in")

            Result.success(execute(userId))
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(userId: Long): R
}