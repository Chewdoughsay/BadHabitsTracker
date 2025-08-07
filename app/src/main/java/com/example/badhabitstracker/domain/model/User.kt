package com.example.badhabitstracker.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
class User(
    val id: Long = 0,
    val email: String,
    val name: String,
    val joinDate: Date,
    val totalHabits: Int = 0,
    val settings: UserSettings = UserSettings(),
    val profileImageUrl: String? = null,
    val isVerified: Boolean = false,
    val lastLoginDate: Date? = null
): Parcelable {
    /**
     * formatare joindate pt afisare
     */
    fun getFormattedJoinDate(): String {
        return android.text.format.DateFormat.format("MMM yyyy", joinDate).toString()
    }

    /**
     * calc nr de zile de la joindate pana acum (zile intregi)
     */
    fun getDaysSinceJoin(): Int {
        val now = Date()
        val diffInMs = now.time - joinDate.time
        return (diffInMs / (1000 * 60 * 60 * 24)).toInt()
    }

    /**
     * get display name (primu cuvant din nume sau ce e inainte de @ in email)
     */
    fun getDisplayName(): String {
        return if (name.isNotBlank()) {
            name.split(" ").first()
        } else {
            email.substringBefore("@")
        }
    }
}