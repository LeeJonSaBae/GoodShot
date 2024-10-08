package com.ijonsabae.domain.usecase.replay

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.SwingComparisonParam
import com.ijonsabae.domain.model.SwingFeedbackExportImportParam

interface ImportSwingFeedbackListUseCase {
    suspend operator fun invoke(swingComparisonParam: SwingComparisonParam) : Result<CommonResponse<List<SwingFeedbackExportImportParam>>>
}