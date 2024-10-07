package com.ijonsabae.domain.usecase.replay

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.SwingFeedbackExportParam

interface ExportSwingFeedbackListUseCase {
    suspend operator fun invoke(swingFeedbackExportParamList: List<SwingFeedbackExportParam>): Result<CommonResponse<Unit>>
}