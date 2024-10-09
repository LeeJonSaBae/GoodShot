package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.usecase.replay.DeleteLocalSwingFeedbackUseCase
import javax.inject.Inject

class DeleteLocalSwingFeedbackUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
): DeleteLocalSwingFeedbackUseCase{
    override suspend operator fun invoke(userId: Long, videoName: String): Int {
        return swingFeedbackRepository.deleteFeedback(userId, videoName)
    }
}