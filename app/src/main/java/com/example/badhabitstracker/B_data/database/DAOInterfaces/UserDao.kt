package com.example.badhabitstracker.B_data.database

import androidx.room.*
import com.example.badhabitstracker.B_data.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // Basic CRUD operations
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    // Query operations
    @Query("SELECT * FROM users WHERE user_id = :userId")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE user_id = :userId")
    fun getUserByIdFlow(userId: Long): Flow<UserEntity?>

    // Authentication queries
    @Query("SELECT * FROM users WHERE email = :email AND password_hash = :passwordHash LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, passwordHash: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun isEmailExists(email: String): Int

    // Profile updates
    @Query("UPDATE users SET profile_image_url = :imageUrl, updated_at = :updatedAt WHERE user_id = :userId")
    suspend fun updateProfileImage(userId: Long, imageUrl: String, updatedAt: Long)

    @Query("UPDATE users SET password_hash = :newPasswordHash, updated_at = :updatedAt WHERE user_id = :userId")
    suspend fun updatePassword(userId: Long, newPasswordHash: String, updatedAt: Long)

    @Query("UPDATE users SET last_login_date = :loginDate, updated_at = :updatedAt WHERE user_id = :userId")
    suspend fun updateLastLoginDate(userId: Long, loginDate: Long, updatedAt: Long)

    // Settings updates - handled through UserSettings embedded object updates
    @Query("UPDATE users SET updated_at = :updatedAt WHERE user_id = :userId")
    suspend fun touchUpdatedAt(userId: Long, updatedAt: Long)

    // Statistics
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getTotalUsersCount(): Int

    // Cleanup operations
    @Query("DELETE FROM users WHERE user_id = :userId")
    suspend fun deleteUserById(userId: Long)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}