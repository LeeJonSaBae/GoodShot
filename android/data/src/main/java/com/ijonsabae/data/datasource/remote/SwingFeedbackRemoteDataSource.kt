package com.ijonsabae.data.datasource.remote

import com.ijonsabae.data.retrofit.SwingService
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.SwingComparisonParam
import com.ijonsabae.domain.model.SwingFeedbackDataNeedToUpload
import com.ijonsabae.domain.model.SwingFeedbackExportImportParam
import com.ijonsabae.domain.model.SwingFeedbackSync
import javax.inject.Inject

class SwingFeedbackRemoteDataSource @Inject constructor(
    private val swingService: SwingService
){
    suspend fun syncSwingFeedback(swingFeedbackSyncList: List<SwingFeedbackSync>): Result<CommonResponse<Unit>> {
        return swingService.syncSwingFeedback(swingFeedbackSyncList)
    }

    suspend fun comparisonSwingFeedback(swingComparisonParam: SwingComparisonParam): Result<CommonResponse<List<SwingFeedbackDataNeedToUpload>>>{
        return swingService.comparisonSwingFeedback(swingComparisonParam)
    }

    suspend fun exportSwingFeedback(swingFeedbackExportParamList: List<SwingFeedbackExportImportParam>): Result<CommonResponse<Unit>>{
        return swingService.exportSwingFeedback(swingFeedbackExportParamList)
    }

    suspend fun importSwingFeedback(swingComparisonParam: SwingComparisonParam): Result<CommonResponse<List<SwingFeedbackExportImportParam>>>{
        return swingService.importSwingFeedback(swingComparisonParam)
    }
}