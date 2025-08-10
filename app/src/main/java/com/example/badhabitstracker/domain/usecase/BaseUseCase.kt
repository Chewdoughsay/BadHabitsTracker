package com.example.badhabitstracker.domain.usecase

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