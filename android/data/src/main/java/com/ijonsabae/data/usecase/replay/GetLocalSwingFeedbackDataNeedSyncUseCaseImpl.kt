package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.SwingFeedbackSync
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackDataNeedSyncUseCase
import javax.inject.Inject

class GetLocalSwingFeedbackDataNeedSyncUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
): GetLocalSwingFeedbackDataNeedSyncUseCase {
    override suspend operator fun invoke(swingFeedbackSyncList: List<SwingFeedbackSync>): Result<CommonResponse<Unit>>{
        return swingFeedbackRepository.syncSwingFeedbackData(swingFeedbackSyncList)
    }
}