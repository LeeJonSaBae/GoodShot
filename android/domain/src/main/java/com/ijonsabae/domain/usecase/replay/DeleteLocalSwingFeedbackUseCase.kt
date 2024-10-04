package com.ijonsabae.domain.usecase.replay

interface DeleteLocalSwingFeedbackUseCase {
    operator fun invoke(userId: Long, videoName: String): Int
}