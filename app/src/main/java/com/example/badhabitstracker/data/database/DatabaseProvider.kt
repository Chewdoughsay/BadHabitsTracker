package com.example.badhabitstracker.data.database

import android.content.Context
import androidx.room.Room

/**
 * Database provider for creating and managing the Room database instance
 * This class follows the Singleton pattern to ensure single database instance
 */
object DatabaseProvider {

    @Volatile
    private var INSTANCE: BadHabitsDatabase? = null

    /**
     * Get the singleton database instance
     * Uses double-checked locking pattern for thread safety
     */
    fun getDatabase(context: Context): BadHabitsDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = buildDatabase(context)
            INSTANCE = instance
            instance
        }
    }

    /**
     * Build the Room database instance with all necessary configurations
     */
    private fun buildDatabase(context: Context): BadHabitsDatabase {
        return Room.databaseBuilder(
            context = context.applicationContext,
            klass = BadHabitsDatabase::class.java,
            name = BadHabitsDatabase.DATABASE_NAME
        )
            // Add type converters
            .addTypeConverter(Converters())

            // Add database callback for initial setup
            .addCallback(BadHabitsDatabase.databaseCallback)

            // Add migrations when needed (currently none for version 1)
            // .addMigrations(MIGRATION_1_2, MIGRATION_2_3, ...)

            // For development: allow main thread queries (remove in production)
            // .allowMainThreadQueries()

            // For development: fall back to destructive migration if no migration provided
            // WARNING: This will delete all data! Only use in development
            .fallbackToDestructiveMigration()

            // Build the database
            .build()
    }

    /**
     * Close the database instance (useful for testing or app shutdown)
     */
    fun closeDatabase() {
        INSTANCE?.close()
        INSTANCE = null
    }

    /**
     * Clear all data from database (useful for logout or data reset)
     * WARNING: This will delete all user data!
     */
    suspend fun clearAllData() {
        INSTANCE?.let { database ->
            database.clearAllTables()
        }
    }

    /**
     * Get individual DAOs for dependency injection
     */
    fun provideUserDao(context: Context): UserDao {
        return getDatabase(context).userDao()
    }

    fun provideHabitDao(context: Context): HabitDao {
        return getDatabase(context).habitDao()
    }

    fun provideHabitEntryDao(context: Context): HabitEntryDao {
        return getDatabase(context).habitEntryDao()
    }

    fun provideAchievementDao(context: Context): AchievementDao {
        return getDatabase(context).achievementDao()
    }
}

/**
 * Extension functions for database maintenance
 */
suspend fun BadHabitsDatabase.deleteAllUserData(userId: Long) {
    // Delete in correct order to avoid foreign key constraint violations
    achievementDao().deleteAllAchievementsForUser(userId)
    habitEntryDao().deleteAllEntriesForUser(userId)
    habitDao().deleteAllHabitsForUser(userId)
    userDao().deleteUserById(userId)
}

suspend fun BadHabitsDatabase.deleteAllHabitData(habitId: Long) {
    // Delete in correct order to avoid foreign key constraint violations
    achievementDao().deleteAllAchievementsForHabit(habitId)
    habitEntryDao().deleteAllEntriesForHabit(habitId)
    habitDao().deleteHabitById(habitId)
}