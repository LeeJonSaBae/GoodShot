package com.ijonsabae.domain.usecase.replay

import com.ijonsabae.domain.model.SwingFeedbackComment

interface InsertLocalSwingFeedbackCommentUseCase {
    operator fun invoke(swingFeedbackCommentEntity: SwingFeedbackComment)
}