package com.example.badhabitstracker.A_domain.usecase

import com.example.badhabitstracker.A_domain.model.Achievement
import com.example.badhabitstracker.A_domain.repository.AchievementRepository
import com.example.badhabitstracker.A_domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetAllAchievementsUseCase(
    private val achievementRepository: AchievementRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUserNoParams<Flow<List<Achievement>>>(userRepository) {

    override suspend fun execute(userId: Long): Flow<List<Achievement>> {
        return achievementRepository.getAllAchievements(userId)
    }
}