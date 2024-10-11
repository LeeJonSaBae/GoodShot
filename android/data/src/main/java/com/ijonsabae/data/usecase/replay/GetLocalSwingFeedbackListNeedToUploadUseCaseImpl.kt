package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackListNeedToUploadUseCase
import javax.inject.Inject

class GetLocalSwingFeedbackListNeedToUploadUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
): GetLocalSwingFeedbackListNeedToUploadUseCase{
    override suspend fun invoke(userID:Long): List<SwingFeedback> {
        return swingFeedbackRepository.getSwingFeedbackListNeedToUpload(userID)
    }
}