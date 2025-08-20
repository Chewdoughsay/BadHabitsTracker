package com.example.badhabitstracker.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.badhabitstracker.domain.model.Habit
import com.example.badhabitstracker.domain.model.HabitCategory
import java.util.Date

@Entity(
    tableName = "habits",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE // Delete habits when user is deleted
        )
    ],
    indices = [
        Index(value = ["user_id"]), // Index for faster queries by userId
        Index(value = ["user_id", "is_active"]), // Composite index for active habits queries
        Index(value = ["category"]) // Index for category filters
    ]
)
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "habit_id")
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "category")
    val category: HabitCategory,

    @ColumnInfo(name = "start_date")
    val startDate: Date,

    @ColumnInfo(name = "target_days")
    val targetDays: Int? = null,

    @ColumnInfo(name = "current_streak")
    val currentStreak: Int = 0,

    @ColumnInfo(name = "longest_streak")
    val longestStreak: Int = 0,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "daily_cost")
    val dailyCost: Double? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Date = Date()
)
fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = this.id,
        userId = this.userId,
        name = this.name,
        description = this.description,
        category = this.category,
        startDate = this.startDate,
        targetDays = this.targetDays,
        currentStreak = this.currentStreak,
        longestStreak = this.longestStreak,
        isActive = this.isActive,
        dailyCost = this.dailyCost,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun Habit.toEntity(): HabitEntity {
    return HabitEntity(
        id = this.id,
        userId = this.userId,
        name = this.name,
        description = this.description,
        category = this.category,
        startDate = this.startDate,
        targetDays = this.targetDays,
        currentStreak = this.currentStreak,
        longestStreak = this.longestStreak,
        isActive = this.isActive,
        dailyCost = this.dailyCost,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
