package com.ijonsabae.data.usecase.home

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.usecase.home.GetLastSwingDataUseCase
import javax.inject.Inject

class GetLastSwingDataUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
): GetLastSwingDataUseCase{
    override suspend fun invoke(userID: Long): SwingFeedback {
        return swingFeedbackRepository.getLastItem(userID)
    }

}