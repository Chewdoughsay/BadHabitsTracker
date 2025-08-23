package com.example.badhabitstracker.A_domain.usecase

import com.example.badhabitstracker.A_domain.repository.AchievementRepository
import com.example.badhabitstracker.A_domain.repository.UserRepository

/**
 * get count of unviewed achievements for badge
 */
class GetUnviewedAchievementsCountUseCase(
    private val achievementRepository: AchievementRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUserNoParams<Int>(userRepository) {

    override suspend fun execute(userId: Long): Int {
        return achievementRepository.getUnviewedAchievementsCount(userId)
    }
}