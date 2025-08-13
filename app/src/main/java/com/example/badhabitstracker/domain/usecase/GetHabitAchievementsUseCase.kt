package com.example.badhabitstracker.domain.usecase

import com.example.badhabitstracker.domain.model.Achievement
import com.example.badhabitstracker.domain.repository.AchievementRepository
import com.example.badhabitstracker.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow


/**
 * get achivement pt habit specific
 */
class GetHabitAchievementsUseCase(
    private val achievementRepository: AchievementRepository,
    userRepository: UserRepository
) : BaseUseCaseWithCurrentUser<Long, Flow<List<Achievement>>>(userRepository) {

    override suspend fun execute(parameters: Long, userId: Long): Flow<List<Achievement>> {
        return achievementRepository.getAchievementsForHabit(parameters)
    }
}