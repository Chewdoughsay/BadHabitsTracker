package com.example.badhabitstracker.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

import java.util.Date

@Parcelize
data class Achievement(
    val id: Long = 0,
    val habitId: Long,
    val userId: Long,
    val type: AchievementType,
    val unlockedAt: Date,
    val title: String,
    val description: String,
    val iconResource: String? = null,
    val isViewed: Boolean = false
) : Parcelable {

    /**
     * formatare data pt afisare
     */
    fun getFormattedDate(): String {
        return android.text.format.DateFormat.format("MMM dd, yyyy", unlockedAt).toString()
    }

    /**
     * verif daca a fost deblocat recent
     */
    fun isRecent(): Boolean {
        val now = Date()
        val timeDiff = now.time - unlockedAt.time
        val hoursDiff = timeDiff / (1000 * 60 * 60)
        return hoursDiff <= 24
    }

    companion object {
        /**
         * creaza achivement pt un milestone specific
         */
        fun createMilestone(
            habitId: Long,
            userId: Long,
            type: AchievementType,
            customTitle: String? = null): Achievement {
            return Achievement(
                habitId = habitId,
                userId = userId,
                type = type,
                unlockedAt = Date(),
                title = customTitle ?: type.displayName,
                description = type.description
            )
        }
    }
}