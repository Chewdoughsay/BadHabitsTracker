package com.example.badhabitstracker.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Habit(
    val id: Long = 0,
    val name: String,
    val description: String,
    val category: HabitCategory,
    val startDate: Date,
    val targetDays: Int? = null,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val isActive: Boolean = true,
    val dailyCost: Double? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
) : Parcelable {

    /**
     * calculeaza progresu bazat pe targetul de zile,
     * daca targetul e 0 sau null, returneaza null
     */
    fun getProgressPercentage(): Int? {
        return targetDays?.let { target ->
            if (target > 0) {
                ((currentStreak.toDouble() / target) * 100).toInt().coerceAtMost(100)
            } else null
        }
    }

    /**
     * calc totalul de bani economisiti in timpu streakului
     */
    fun getMoneySaved(): Double {
        return dailyCost?.let { cost ->
            currentStreak * cost
        } ?: 0.0
    }

    /**
     * verifica daca am atins targetul propus
     */
    fun isTargetReached(): Boolean {
        return targetDays?.let { target ->
            currentStreak >= target
        } ?: false
    }

    /**
     * calc cate zile mai sunt pana la target
     * returneaza null daca targetul e null
     */
    fun getDaysRemaining(): Int? {
        return targetDays?.let { target ->
            (target - currentStreak).coerceAtLeast(0)
        }
    }
}