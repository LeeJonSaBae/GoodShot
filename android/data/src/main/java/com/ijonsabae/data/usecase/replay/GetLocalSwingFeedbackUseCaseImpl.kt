package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackUseCase
import javax.inject.Inject

class GetLocalSwingFeedbackUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
): GetLocalSwingFeedbackUseCase {
    override operator fun invoke(userID:Long, videoName: String): SwingFeedback{
        return swingFeedbackRepository.getSwingFeedback(userID, videoName)
    }

}