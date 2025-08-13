package com.example.badhabitstracker.domain.usecase

import com.example.badhabitstracker.domain.model.Achievement
import com.example.badhabitstracker.domain.repository.AchievementRepository
import com.example.badhabitstracker.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetAllAchievementsUseCase(
    private val achievementRepository: AchievementRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUserNoParams<Flow<List<Achievement>>>(userRepository) {

    override suspend fun execute(userId: Long): Flow<List<Achievement>> {
        return achievementRepository.getAllAchievements(userId)
    }
}