package com.ijonsabae.data.retrofit

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.SwingComparisonParam
import com.ijonsabae.domain.model.SwingFeedbackDataNeedToUpload
import com.ijonsabae.domain.model.SwingFeedbackExportImportParam
import com.ijonsabae.domain.model.SwingFeedbackSync
import com.ijonsabae.domain.model.TotalReport
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SwingService {
    @POST("swings/sync")
    suspend fun syncSwingFeedback(@Body syncData: List<SwingFeedbackSync>): Result<CommonResponse<Unit>>

    @POST("swings/comparison")
    suspend fun comparisonSwingFeedback(@Body swingComparisonParam: SwingComparisonParam): Result<CommonResponse<List<SwingFeedbackDataNeedToUpload>>>
    @POST("swings/export")
    suspend fun exportSwingFeedback(@Body swingFeedbackExportParamList: List<SwingFeedbackExportImportParam>): Result<CommonResponse<Unit>>
    @GET("swings/report")
    suspend fun getTotalReport(): Result<CommonResponse<TotalReport>>
    @POST("swings/import")
    suspend fun importSwingFeedback(@Body swingComparisonParam: SwingComparisonParam): Result<CommonResponse<List<SwingFeedbackExportImportParam>>>
}