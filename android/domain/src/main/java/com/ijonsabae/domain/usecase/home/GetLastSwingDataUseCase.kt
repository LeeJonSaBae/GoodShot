package com.ijonsabae.domain.usecase.home

import com.ijonsabae.domain.model.SwingFeedback

interface GetLastSwingDataUseCase {
    suspend operator fun invoke(userID: Long): SwingFeedback
}