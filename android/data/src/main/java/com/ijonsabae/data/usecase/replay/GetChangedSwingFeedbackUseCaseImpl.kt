package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.model.SwingFeedbackSyncRoomData
import com.ijonsabae.domain.usecase.replay.GetChangedSwingFeedbackUseCase
import javax.inject.Inject

class GetChangedSwingFeedbackUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
): GetChangedSwingFeedbackUseCase {
    override operator fun invoke(userID: Long): List<SwingFeedbackSyncRoomData> {
        return swingFeedbackRepository.getChangedSwingFeedback(userID)
    }
}