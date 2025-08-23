package com.example.badhabitstracker.B_data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index
import com.example.badhabitstracker.A_domain.model.Achievement
import com.example.badhabitstracker.A_domain.model.AchievementType
import java.util.Date

@Entity(
    tableName = "achievements",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["habit_id"],
            childColumns = ["habit_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]), // Index for user queries
        Index(value = ["habit_id"]), // Index for habit queries
        Index(value = ["habit_id", "type"], unique = true), // Unique constraint: one achievement type per habit
        Index(value = ["unlocked_at"]), // Index for chronological queries
        Index(value = ["is_viewed"]), // Index for unviewed achievements
        Index(value = ["type"]) // Index for achievement type queries
    ]
)
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "achievement_id")
    val id: Long = 0,

    @ColumnInfo(name = "habit_id")
    val habitId: Long,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "type")
    val type: AchievementType,

    @ColumnInfo(name = "unlocked_at")
    val unlockedAt: Date,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "icon_resource")
    val iconResource: String? = null,

    @ColumnInfo(name = "is_viewed")
    val isViewed: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date()
)

// Extension functions for mapping between Domain and Data layer
fun AchievementEntity.toDomain(): Achievement {
    return Achievement(
        id = this.id,
        habitId = this.habitId,
        userId = this.userId,
        type = this.type,
        unlockedAt = this.unlockedAt,
        title = this.title,
        description = this.description,
        iconResource = this.iconResource,
        isViewed = this.isViewed
    )
}

fun Achievement.toEntity(): AchievementEntity {
    return AchievementEntity(
        id = this.id,
        habitId = this.habitId,
        userId = this.userId,
        type = this.type,
        unlockedAt = this.unlockedAt,
        title = this.title,
        description = this.description,
        iconResource = this.iconResource,
        isViewed = this.isViewed,
        createdAt = Date()
    )
}