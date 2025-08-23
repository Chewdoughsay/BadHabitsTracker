package com.example.badhabitstracker.B_data.database

import androidx.room.TypeConverter
import com.example.badhabitstracker.A_domain.model.AchievementType
import com.example.badhabitstracker.A_domain.model.HabitCategory
import com.example.badhabitstracker.A_domain.model.MoodLevel
import java.util.Date

class Converters {

    // Date converters
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // HabitCategory converters
    @TypeConverter
    fun fromHabitCategory(category: HabitCategory): String {
        return category.name
    }

    @TypeConverter
    fun toHabitCategory(categoryString: String): HabitCategory {
        return try {
            HabitCategory.valueOf(categoryString)
        } catch (e: IllegalArgumentException) {
            HabitCategory.OTHER // Default fallback
        }
    }

    // MoodLevel converters
    @TypeConverter
    fun fromMoodLevel(mood: MoodLevel?): String? {
        return mood?.name
    }

    @TypeConverter
    fun toMoodLevel(moodString: String?): MoodLevel? {
        return moodString?.let {
            try {
                MoodLevel.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null // Return null for invalid mood levels
            }
        }
    }

    // AchievementType converters
    @TypeConverter
    fun fromAchievementType(type: AchievementType): String {
        return type.name
    }

    @TypeConverter
    fun toAchievementType(typeString: String): AchievementType {
        return try {
            AchievementType.valueOf(typeString)
        } catch (e: IllegalArgumentException) {
            AchievementType.MILESTONE // Default fallback
        }
    }
}