package com.example.badhabitstracker.data.database

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.badhabitstracker.data.database.entities.AchievementEntity
import com.example.badhabitstracker.data.database.entities.HabitEntity
import com.example.badhabitstracker.data.database.entities.HabitEntryEntity
import com.example.badhabitstracker.data.database.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        HabitEntity::class,
        HabitEntryEntity::class,
        AchievementEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class BadHabitsDatabase : RoomDatabase() {

    // DAO access methods
    abstract fun userDao(): UserDao
    abstract fun habitDao(): HabitDao
    abstract fun habitEntryDao(): HabitEntryDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        const val DATABASE_NAME = "bad_habits_database"

        // Future migrations will be added here
        // Example migration from version 1 to 2:
        /*
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Migration SQL statements
                database.execSQL("ALTER TABLE habits ADD COLUMN new_column TEXT")
            }
        }
        */

        // Database creation callback for initial setup
        val databaseCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Any initial setup can be done here
                // For example, inserting default data
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Enable foreign key constraints
                db.execSQL("PRAGMA foreign_keys=ON")
            }
        }

        // Prepopulated data for achievements types (optional)
        private fun populateDefaultData(database: SupportSQLiteDatabase) {
            // This could be used to insert default achievement templates
            // or other reference data
        }
    }
}

// Database creation helper functions
object DatabaseConstants {
    const val DATABASE_NAME = "bad_habits_database"
    const val DATABASE_VERSION = 1

    // Table names (useful for raw queries if needed)
    const val USERS_TABLE = "users"
    const val HABITS_TABLE = "habits"
    const val HABIT_ENTRIES_TABLE = "habit_entries"
    const val ACHIEVEMENTS_TABLE = "achievements"

    // Common column names
    const val ID_COLUMN = "id"
    const val USER_ID_COLUMN = "user_id"
    const val HABIT_ID_COLUMN = "habit_id"
    const val CREATED_AT_COLUMN = "created_at"
    const val UPDATED_AT_COLUMN = "updated_at"
}