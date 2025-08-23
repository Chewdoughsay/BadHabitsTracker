package com.example.badhabitstracker.A_domain.model

enum class AchievementType(val displayName: String, val description: String) {
    FIRST_DAY("First Day", "Started your journey"),
    WEEK_STREAK("Week Warrior", "7 consecutive days clean"),
    MONTH_STREAK("Month Master", "30 consecutive days clean"),
    HUNDRED_DAYS("Century Club", "100 consecutive days clean"),
    FIRST_HABIT("Habit Hero", "Created your first habit"),
    MULTIPLE_HABITS("Multi-Tasker", "Managing 3+ habits"),
    COMEBACK("Phoenix", "Restarted after a setback"),
    MILESTONE("Milestone", "Reached target goal")
}