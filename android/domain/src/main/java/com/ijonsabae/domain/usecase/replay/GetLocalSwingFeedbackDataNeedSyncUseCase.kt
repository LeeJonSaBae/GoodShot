package com.ijonsabae.domain.usecase.replay

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.SwingFeedbackSync

interface GetLocalSwingFeedbackDataNeedSyncUseCase {
    suspend operator fun invoke(swingFeedbackSyncList: List<SwingFeedbackSync>): Result<CommonResponse<Unit>>
}