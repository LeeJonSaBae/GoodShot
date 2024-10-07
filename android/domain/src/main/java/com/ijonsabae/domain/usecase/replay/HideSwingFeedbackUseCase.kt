package com.ijonsabae.domain.usecase.replay

interface HideSwingFeedbackUseCase {
    operator fun invoke(userID: Long, swingCode: String, currentTime: Long) : Int
}