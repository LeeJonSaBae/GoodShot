package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.usecase.replay.SyncUpdateStatusUseCase
import javax.inject.Inject

class SyncUpdateStatusUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
): SyncUpdateStatusUseCase {
    override suspend fun invoke(userID: Long): Int {
        return swingFeedbackRepository.syncUpdateStatus(userID)
    }

}