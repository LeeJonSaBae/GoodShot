package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.usecase.replay.UpdateClampStatusUseCase
import com.ijonsabae.domain.usecase.replay.UpdateLikeStatusUseCase
import javax.inject.Inject

class UpdateClampStatusUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
): UpdateClampStatusUseCase {
    override suspend fun invoke(userID: Long, swingCode: String, clampStatus: Boolean): Int {
        return swingFeedbackRepository.updateClampStatus(userID, swingCode, clampStatus)
    }
}