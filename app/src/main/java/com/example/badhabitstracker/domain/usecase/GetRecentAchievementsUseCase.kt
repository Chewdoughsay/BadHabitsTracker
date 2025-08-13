package com.example.badhabitstracker.domain.usecase

import com.example.badhabitstracker.domain.model.Achievement
import com.example.badhabitstracker.domain.repository.AchievementRepository
import com.example.badhabitstracker.domain.repository.UserRepository

/**
 * achievementuri recente pt dashboard
 */
class GetRecentAchievementsUseCase(
    private val achievementRepository: AchievementRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUser<Int, List<Achievement>>(userRepository) {

    override suspend fun execute(parameters: Int, userId: Long): List<Achievement> {
        return achievementRepository.getRecentAchievements(userId, parameters)
    }
}