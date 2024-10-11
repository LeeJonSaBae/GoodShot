package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.SwingComparisonParam
import com.ijonsabae.domain.model.SwingFeedbackExportImportParam
import com.ijonsabae.domain.usecase.replay.ImportSwingFeedbackListUseCase
import javax.inject.Inject

class ImportSwingFeedbackListUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
): ImportSwingFeedbackListUseCase{
    override suspend fun invoke(swingComparisonParam: SwingComparisonParam) : Result<CommonResponse<List<SwingFeedbackExportImportParam>>> {
        return swingFeedbackRepository.importSwingFeedback(swingComparisonParam)
    }

}