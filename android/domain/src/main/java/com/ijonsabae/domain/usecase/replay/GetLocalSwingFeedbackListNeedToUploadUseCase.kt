package com.ijonsabae.domain.usecase.replay

import com.ijonsabae.domain.model.SwingFeedback

interface GetLocalSwingFeedbackListNeedToUploadUseCase {
    suspend operator fun invoke(userID:Long): List<SwingFeedback>
}