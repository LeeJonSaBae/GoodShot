package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.usecase.replay.UpdateLikeStatusUseCase
import com.ijonsabae.domain.usecase.replay.UpdateTitleUseCase
import javax.inject.Inject

class UpdateTitleUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
): UpdateTitleUseCase {
    override suspend fun invoke(userID: Long, swingCode: String, title: String, currentTime: Long): Int {
        return swingFeedbackRepository.updateTitle(userID, swingCode, title, currentTime)
    }
}