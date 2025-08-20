package com.example.badhabitstracker.data.repository

import com.example.badhabitstracker.data.database.UserDao
import com.example.badhabitstracker.data.database.entities.toDomain
import com.example.badhabitstracker.data.database.entities.toEntity
import com.example.badhabitstracker.domain.model.User
import com.example.badhabitstracker.domain.model.UserSettings
import com.example.badhabitstracker.domain.repository.SharedPreferencesRepository
import com.example.badhabitstracker.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.util.Date

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : UserRepository {

    // Current user cache for performance
    private var currentUserCache: User? = null

    override suspend fun registerUser(email: String, password: String, name: String): Result<User> {
        return try {
            // Check if email already exists
            val existingUser = userDao.getUserByEmail(email.lowercase().trim())
            if (existingUser != null) {
                return Result.failure(Exception("Email already exists"))
            }

            // Create new user
            val passwordHash = hashPassword(password)
            val newUser = User(
                email = email.lowercase().trim(),
                name = name.trim(),
                joinDate = Date()
            )

            // Insert into database
            val userId = userDao.insertUser(newUser.toEntity(passwordHash))
            val registeredUser = newUser.copy(id = userId)

            // Cache the user
            currentUserCache = registeredUser

            Result.success(registeredUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val passwordHash = hashPassword(password)
            val userEntity = userDao.getUserByEmailAndPassword(
                email.lowercase().trim(),
                passwordHash
            )

            if (userEntity != null) {
                val user = userEntity.toDomain()

                // Update last login date
                userDao.updateLastLoginDate(
                    userId = user.id,
                    loginDate = Date().time,
                    updatedAt = Date().time
                )

                // Cache the user
                currentUserCache = user

                Result.success(user)
            } else {
                Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logoutUser() {
        // Clear session
        sharedPreferencesRepository.clearUserId()

        // Clear cache
        currentUserCache = null
    }

    override suspend fun getCurrentUser(): User? {
        // Return cached user if available
        currentUserCache?.let { return it }

        // Try to get user from session
        val userId = sharedPreferencesRepository.getUserId() ?: return null

        return try {
            val userEntity = userDao.getUserById(userId)
            val user = userEntity?.toDomain()

            // Cache the user
            currentUserCache = user

            user
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getCurrentUserFlow(): Flow<User?> {
        val userId = sharedPreferencesRepository.getUserId() ?: return flowOf(null)

        return userDao.getUserByIdFlow(userId).map { userEntity ->
            userEntity?.toDomain()?.also { user ->
                // Update cache when Flow emits
                currentUserCache = user
            }
        }
    }

    override suspend fun updateUser(user: User) {
        try {
            // Get current password hash (we can't update password through this method)
            val currentUserEntity = userDao.getUserById(user.id)
            val passwordHash = currentUserEntity?.passwordHash ?: ""

            // Update user in database
            userDao.updateUser(user.toEntity(passwordHash))

            // Update cache
            currentUserCache = user

            // Update session settings in SharedPreferences
            sharedPreferencesRepository.saveUserSettings(user.id, user.settings)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteUser(userId: Long) {
        try {
            // Clear all user data from SharedPreferences
            sharedPreferencesRepository.clearUserData(userId)

            // Delete user from database (cascades to all related data)
            userDao.deleteUserById(userId)

            // Clear cache if it's the current user
            if (currentUserCache?.id == userId) {
                currentUserCache = null
                sharedPreferencesRepository.clearUserId()
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getUserById(userId: Long): User? {
        return try {
            userDao.getUserById(userId)?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return sharedPreferencesRepository.isUserSessionActive() && getCurrentUser() != null
    }

    override suspend fun saveUserSession(user: User) {
        // Save user ID to SharedPreferences for session persistence
        sharedPreferencesRepository.saveUserId(user.id)

        // Save user settings
        sharedPreferencesRepository.saveUserSettings(user.id, user.settings)

        // Cache the user
        currentUserCache = user
    }

    override suspend fun clearUserSession() {
        sharedPreferencesRepository.clearUserId()
        currentUserCache = null
    }

    override suspend fun getCurrentUserId(): Long? {
        return sharedPreferencesRepository.getUserId()
    }

    override suspend fun getUserSettings(userId: Long): UserSettings? {
        return try {
            // Try SharedPreferences first (faster)
            val settingsFromPrefs = sharedPreferencesRepository.getUserSettings(userId)
            if (settingsFromPrefs != UserSettings()) {
                return settingsFromPrefs
            }

            // Fallback to database
            userDao.getUserById(userId)?.settings
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateUserSettings(userId: Long, settings: UserSettings) {
        try {
            // Update in SharedPreferences
            sharedPreferencesRepository.saveUserSettings(userId, settings)

            // Update in database
            val currentUser = userDao.getUserById(userId)
            if (currentUser != null) {
                val updatedUser = currentUser.copy(
                    settings = settings,
                    updatedAt = Date()
                )
                userDao.updateUser(updatedUser)

                // Update cache if it's the current user
                if (currentUserCache?.id == userId) {
                    currentUserCache = currentUserCache?.copy(settings = settings)
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateProfileImage(userId: Long, imageUrl: String) {
        try {
            userDao.updateProfileImage(
                userId = userId,
                imageUrl = imageUrl,
                updatedAt = Date().time
            )

            // Update cache if it's the current user
            if (currentUserCache?.id == userId) {
                currentUserCache = currentUserCache?.copy(profileImageUrl = imageUrl)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updatePassword(userId: Long, oldPassword: String, newPassword: String): Result<Unit> {
        return try {
            // Verify old password
            val currentUser = userDao.getUserById(userId)
            if (currentUser == null) {
                return Result.failure(Exception("User not found"))
            }

            val oldPasswordHash = hashPassword(oldPassword)
            if (currentUser.passwordHash != oldPasswordHash) {
                return Result.failure(Exception("Current password is incorrect"))
            }

            // Update with new password
            val newPasswordHash = hashPassword(newPassword)
            userDao.updatePassword(
                userId = userId,
                newPasswordHash = newPasswordHash,
                updatedAt = Date().time
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Simple password hashing using SHA-256
     * Note: In production, use more secure hashing like bcrypt or scrypt
     */
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}