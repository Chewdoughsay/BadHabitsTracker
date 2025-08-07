package com.example.badhabitstracker.domain.model

enum class HabitCategory(val displayName: String, val icon: String) {
    SMOKING("Smoking", "🚭"),
    SOCIAL_MEDIA("Social Media", "📱"),
    JUNK_FOOD("Junk Food", "🍔"),
    ALCOHOL("Alcohol", "🍺"),
    PROCRASTINATION("Procrastination", "⏰"),
    GAMING("Gaming", "🎮"),
    SPENDING("Overspending", "💸"),
    CAFFEINE("Caffeine", "☕"),
    NAIL_BITING("Nail Biting", "💅"),
    OTHER("Other", "📝")
}