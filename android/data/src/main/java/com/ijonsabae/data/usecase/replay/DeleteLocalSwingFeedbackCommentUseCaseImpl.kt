package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.usecase.replay.DeleteLocalSwingFeedbackCommentUseCase
import javax.inject.Inject

class DeleteLocalSwingFeedbackCommentUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
) : DeleteLocalSwingFeedbackCommentUseCase {
    override operator fun invoke(userId: Long, videoName: String): Int{
        return swingFeedbackRepository.deleteFeedbackComment(userId, videoName)
    }
}