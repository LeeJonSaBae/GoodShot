package com.ijonsabae.domain.usecase.replay

import com.ijonsabae.domain.model.SwingFeedback

interface GetLocalSwingFeedbackUseCase {
    operator fun invoke(userID:Long, videoName: String): SwingFeedback
}