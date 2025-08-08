package com.example.badhabitstracker.domain.repository

import com.example.badhabitstracker.domain.model.User
import com.example.badhabitstracker.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface  UserRepository {

    // authentication
    suspend fun registerUser(email: String, password: String, name: String): Result<User>
    suspend fun loginUser(email: String, password: String): Result<User>
    suspend fun logoutUser()

    // operatii pt user
    suspend fun getCurrentUser(): User?
    fun getCurrentUserFlow(): Flow<User?>
    suspend fun updateUser(user: User)
    suspend fun deleteUser(userId: Long)

    // operatii pt sesiune
    suspend fun isUserLoggedIn(): Boolean
    suspend fun saveUserSession(user: User)
    suspend fun clearUserSession()

    // settings
    suspend fun getUserSettings(userId: Long): UserSettings?
    suspend fun updateUserSettings(userId: Long, settings: UserSettings)

    // profile
    suspend fun updateProfileImage(userId: Long, imageUrl: String)
    suspend fun updatePassword(userId: Long, oldPassword: String, newPassword: String): Result<Unit>
}