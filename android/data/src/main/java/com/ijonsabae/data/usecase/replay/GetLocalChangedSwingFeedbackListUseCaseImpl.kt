package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.model.SwingFeedbackSyncRoomData
import com.ijonsabae.domain.usecase.replay.GetLocalChangedSwingFeedbackListUseCase
import javax.inject.Inject

class GetLocalChangedSwingFeedbackListUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
): GetLocalChangedSwingFeedbackListUseCase {
    override operator fun invoke(userID: Long): List<SwingFeedbackSyncRoomData> {
        return swingFeedbackRepository.getChangedSwingFeedback(userID)
    }
}