package com.ijonsabae.domain.usecase.replay

import com.ijonsabae.domain.model.SwingFeedback

interface GetLocalSwingFeedbackUseCase {
    suspend operator fun invoke(userID:Long, videoName: String): SwingFeedback
}