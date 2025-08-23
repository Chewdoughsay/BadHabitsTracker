package com.example.badhabitstracker.A_domain.model

enum class MoodLevel(val displayName: String, val emoji: String, val value: Int) {
    TERRIBLE("Terrible", "😢", 1),
    BAD("Bad", "😔", 2),
    NEUTRAL("Neutral", "😐", 3),
    GOOD("Good", "😊", 4),
    EXCELLENT("Excellent", "😄", 5)
}