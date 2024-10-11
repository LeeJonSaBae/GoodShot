package com.ijonsabae.domain.usecase.replay

interface HideSwingFeedbackUseCase {
    suspend operator fun invoke(userID: Long, swingCode: String, currentTime: Long) : Int
}