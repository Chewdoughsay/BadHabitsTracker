package com.example.badhabitstracker.domain.usecase

import com.example.badhabitstracker.domain.repository.AchievementRepository

/**
 * mark as viewd (sterge NEW badge)
 */
class MarkAchievementViewedUseCase(
    private val achievementRepository: AchievementRepository
) : BaseUseCase<Long, Unit>() {

    override suspend fun execute(parameters: Long) {
        achievementRepository.markAchievementAsViewed(parameters)
    }
}