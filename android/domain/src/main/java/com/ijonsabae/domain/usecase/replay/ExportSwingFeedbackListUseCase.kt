package com.ijonsabae.domain.usecase.replay

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.SwingFeedbackExportImportParam

interface ExportSwingFeedbackListUseCase {
    suspend operator fun invoke(swingFeedbackExportParamList: List<SwingFeedbackExportImportParam>): Result<CommonResponse<Unit>>
}