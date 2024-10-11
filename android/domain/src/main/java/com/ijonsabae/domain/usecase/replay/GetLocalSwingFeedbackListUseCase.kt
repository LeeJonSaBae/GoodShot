package com.ijonsabae.domain.usecase.replay

import com.ijonsabae.domain.model.SwingFeedback

interface GetLocalSwingFeedbackListUseCase {
    suspend operator fun invoke(userID: Long): List<SwingFeedback>
}