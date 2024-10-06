package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.usecase.replay.UpdateLikeStatusUseCase
import javax.inject.Inject

class UpdateLikeStatusUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
): UpdateLikeStatusUseCase {
    override fun invoke(userID: Long, swingCode: String, likeStatus: Boolean): Int {
        return swingFeedbackRepository.updateLikeStatus(userID, swingCode, likeStatus)
    }
}