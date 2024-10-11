package com.ijonsabae.domain.usecase.replay

interface DeleteLocalSwingFeedbackCommentUseCase {
    suspend operator fun invoke(userId: Long, videoName: String): Int
}