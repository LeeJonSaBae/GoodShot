package com.ijonsabae.data.usecase.home

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.usecase.home.GetSwingDataSizeUseCase
import javax.inject.Inject

class GetSwingDataSizeUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
) : GetSwingDataSizeUseCase{
    override suspend fun invoke(userID: Long): Int {
        return swingFeedbackRepository.getSwingDataSize(userID)
    }
}