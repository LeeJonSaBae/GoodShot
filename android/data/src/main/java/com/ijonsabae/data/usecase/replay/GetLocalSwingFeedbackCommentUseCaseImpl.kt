package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.model.SwingFeedbackComment
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackCommentUseCase
import javax.inject.Inject

class GetLocalSwingFeedbackCommentUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
) :
    GetLocalSwingFeedbackCommentUseCase {
    override operator fun invoke(userID: Long, videoName: String): SwingFeedbackComment{
        return swingFeedbackRepository.getSwingFeedbackComment(userID, videoName)
    }
}