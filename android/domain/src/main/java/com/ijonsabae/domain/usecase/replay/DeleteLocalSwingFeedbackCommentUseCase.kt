package com.ijonsabae.domain.usecase.replay

interface DeleteLocalSwingFeedbackCommentUseCase {
    operator fun invoke(userId: Long, videoName: String): Int
}