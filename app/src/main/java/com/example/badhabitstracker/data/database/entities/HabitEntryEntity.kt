package com.example.badhabitstracker.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index
import com.example.badhabitstracker.domain.model.HabitEntry
import com.example.badhabitstracker.domain.model.MoodLevel
import java.util.Date

@Entity(
    tableName = "habit_entries",
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
        Index(value = ["habit_id", "date"], unique = true), // Unique constraint: one entry per habit per day
        Index(value = ["date"]), // Index for date-based queries
        Index(value = ["was_successful"]) // Index for success rate calculations
    ]
)
data class HabitEntryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "entry_id")
    val id: Long = 0,

    @ColumnInfo(name = "habit_id")
    val habitId: Long,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "date")
    val date: Date,

    @ColumnInfo(name = "was_successful")
    val wasSuccessful: Boolean,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "mood")
    val moodLevel: MoodLevel? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date()
)

// Extension functions for mapping between Domain and Data layer
fun HabitEntryEntity.toDomain(): HabitEntry {
    return HabitEntry(
        id = this.id,
        habitId = this.habitId,
        userId = this.userId,
        date = this.date,
        wasSuccessful = this.wasSuccessful,
        notes = this.notes,
        moodLevel = this.moodLevel,
        createdAt = this.createdAt
    )
}

fun HabitEntry.toEntity(): HabitEntryEntity {
    return HabitEntryEntity(
        id = this.id,
        habitId = this.habitId,
        userId = this.userId,
        date = this.date,
        wasSuccessful = this.wasSuccessful,
        notes = this.notes,
        moodLevel = this.moodLevel,
        createdAt = this.createdAt
    )
}