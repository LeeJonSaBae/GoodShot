package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.SwingFeedbackExportParam
import com.ijonsabae.domain.usecase.replay.ExportSwingFeedbackListUseCase
import javax.inject.Inject

class ExportSwingFeedbackListUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
): ExportSwingFeedbackListUseCase{
    override suspend operator fun invoke(swingFeedbackExportParamList: List<SwingFeedbackExportParam>): Result<CommonResponse<Unit>>{
        return swingFeedbackRepository.exportSwingFeedback(swingFeedbackExportParamList)
    }
}