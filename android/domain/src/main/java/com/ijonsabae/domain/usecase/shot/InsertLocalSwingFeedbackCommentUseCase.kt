package com.ijonsabae.domain.usecase.shot

import com.ijonsabae.domain.model.SwingFeedbackComment

interface InsertLocalSwingFeedbackCommentUseCase {
    suspend operator fun invoke(swingFeedbackCommentEntity: SwingFeedbackComment)
}