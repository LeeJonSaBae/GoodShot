package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.SwingComparisonParam
import com.ijonsabae.domain.model.SwingFeedbackDataNeedToUpload
import com.ijonsabae.domain.usecase.replay.GetRemoteSwingFeedbackListNeedToUploadUseCase
import javax.inject.Inject

class GetRemoteSwingFeedbackListNeedToUploadUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
): GetRemoteSwingFeedbackListNeedToUploadUseCase{
    override suspend operator fun invoke(swingComparisonParam: SwingComparisonParam): Result<CommonResponse<List<SwingFeedbackDataNeedToUpload>>>{
        return swingFeedbackRepository.comparisonSwingFeedback(swingComparisonParam)
    }
}