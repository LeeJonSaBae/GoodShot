package com.ijonsabae.domain.usecase.replay

import com.ijonsabae.domain.model.SwingFeedbackComment

interface GetLocalSwingFeedbackCommentUseCase {
    suspend operator fun invoke(userID: Long, videoName: String): List<SwingFeedbackComment>
}