package com.ijonsabae.domain.usecase.replay

interface DeleteLocalSwingFeedbackUseCase {
    suspend operator fun invoke(userId: Long, videoName: String): Int
}