package com.ijonsabae.domain.usecase.replay

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.SwingComparisonParam
import com.ijonsabae.domain.model.SwingFeedbackDataNeedToUpload

interface GetRemoteSwingFeedbackListNeedToUploadUseCase {
    suspend operator fun invoke(swingComparisonParam: SwingComparisonParam): Result<CommonResponse<List<SwingFeedbackDataNeedToUpload>>>
}