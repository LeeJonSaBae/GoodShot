package com.ijonsabae.domain.usecase.shot

import com.ijonsabae.domain.model.SwingFeedback

interface InsertLocalSwingFeedbackUseCase {
    operator fun invoke(swingFeedbackEntity: SwingFeedback)
}