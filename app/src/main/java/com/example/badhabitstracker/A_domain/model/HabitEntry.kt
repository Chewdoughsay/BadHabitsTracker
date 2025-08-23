package com.example.badhabitstracker.A_domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class HabitEntry (
    val id: Long = 0,
    val userId: Long,
    val habitId: Long,
    val date: Date,
    val wasSuccessful: Boolean,
    val notes: String? = null,
    val moodLevel: MoodLevel? = null,
    val createdAt: Date = Date()
) : Parcelable {

    /**
     * get pt formatare date string pt afisare
     */
    fun getFormattedDate(): String {
        return android.text.format.DateFormat.format("dd MMM yyyy", date).toString()
    }

    /**
     * verif daca entryul e pentru azi
     */
    fun isToday(): Boolean {
        val today = Date()
        val calendar = java.util.Calendar.getInstance()

        calendar.time = date
        val entryDay = calendar.get(java.util.Calendar.DAY_OF_YEAR)
        val entryYear = calendar.get(java.util.Calendar.YEAR)

        calendar.time = today
        val todayDay = calendar.get(java.util.Calendar.DAY_OF_YEAR)
        val todayYear = calendar.get(java.util.Calendar.YEAR)

        return entryDay == todayDay && entryYear == todayYear
    }
}