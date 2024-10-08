package com.ijonsabae.data.usecase.shot

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.model.SwingFeedbackComment
import com.ijonsabae.domain.usecase.shot.InsertLocalSwingFeedbackCommentUseCase
import javax.inject.Inject

class InsertLocalSwingFeedbackCommentUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
) : InsertLocalSwingFeedbackCommentUseCase {
    override operator fun invoke(swingFeedbackCommentEntity: SwingFeedbackComment){
        return swingFeedbackRepository.insertSwingFeedbackComment(swingFeedbackCommentEntity)
    }
}