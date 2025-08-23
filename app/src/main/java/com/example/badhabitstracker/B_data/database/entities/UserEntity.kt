package com.example.badhabitstracker.B_data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.badhabitstracker.A_domain.model.User
import com.example.badhabitstracker.A_domain.model.UserSettings
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    val id: Long = 0,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String, // Hashed password, not plain text

    @ColumnInfo(name = "join_date")
    val joinDate: Date,

    @ColumnInfo(name = "total_habits")
    val totalHabits: Int = 0,

    @Embedded
    val settings: UserSettings,

    @ColumnInfo(name = "profile_image_url")
    val profileImageUrl: String? = null,

    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean = false,

    @ColumnInfo(name = "last_login_date")
    val lastLoginDate: Date? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Date = Date()
)
fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
        name = this.name,
        joinDate = this.joinDate,
        totalHabits = this.totalHabits,
        settings = this.settings,
        profileImageUrl = this.profileImageUrl,
        isVerified = this.isVerified,
        lastLoginDate = this.lastLoginDate
    )
}

fun User.toEntity(passwordHash: String): UserEntity {
    return UserEntity(
        id = this.id,
        email = this.email,
        name = this.name,
        passwordHash = passwordHash,
        joinDate = this.joinDate,
        totalHabits = this.totalHabits,
        settings = this.settings,
        profileImageUrl = this.profileImageUrl,
        isVerified = this.isVerified,
        lastLoginDate = this.lastLoginDate,
        createdAt = Date(),
        updatedAt = Date()
    )
}
