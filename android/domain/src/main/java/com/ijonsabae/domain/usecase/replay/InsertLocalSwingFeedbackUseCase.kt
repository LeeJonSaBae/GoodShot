package com.ijonsabae.domain.usecase.replay

import com.ijonsabae.domain.model.SwingFeedback

interface InsertLocalSwingFeedbackUseCase {
    operator fun invoke(swingFeedbackEntity: SwingFeedback)
}