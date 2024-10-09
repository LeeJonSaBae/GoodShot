package com.ijonsabae.data.repository

import androidx.paging.PagingData
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.SwingComparisonParam
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.model.SwingFeedbackComment
import com.ijonsabae.domain.model.SwingFeedbackDataNeedToUpload
import com.ijonsabae.domain.model.SwingFeedbackExportImportParam
import com.ijonsabae.domain.model.SwingFeedbackSync
import com.ijonsabae.domain.model.SwingFeedbackSyncRoomData
import kotlinx.coroutines.flow.Flow

interface SwingFeedbackRepository {
    suspend fun insertSwingFeedback(swingFeedback: SwingFeedback)
    suspend fun getSwingFeedback(userID: Long, swingCode: String): SwingFeedback
    fun getAllSwingFeedback(userID: Long): Flow<PagingData<SwingFeedback>>
    suspend fun getSwingFeedbackListNeedToUpload(userID:Long): List<SwingFeedback>
    fun getLikeSwingFeedbackList(userID: Long): Flow<PagingData<SwingFeedback>>
    suspend fun deleteFeedback(userId: Long, swingCode: String): Int
    suspend fun insertSwingFeedbackComment(swingFeedbackCommentEntity: SwingFeedbackComment)
    suspend fun getSwingFeedbackComment(userId: Long, swingCode: String): List<SwingFeedbackComment>
    suspend fun deleteFeedbackComment(userID: Long, swingCode: String): Int
    suspend fun updateUserId(oldUserId: Long, newUserId: Long): Int
    suspend fun updateLikeStatus(userID: Long, swingCode: String, likeStatus: Boolean, currentTime: Long): Int
    fun updateClampStatus(userID: Long, swingCode: String, clampStatus: Boolean): Int
    suspend fun updateTitle(userID: Long, swingCode: String, title: String, currentTime: Long): Int
    suspend fun syncUpdateStatus(userID: Long): Int
    suspend fun hideSwingFeedback(userID: Long, swingCode: String, currentTime: Long): Int
    suspend fun getChangedSwingFeedback(userID: Long): List<SwingFeedbackSyncRoomData>
    suspend fun getAllSwingFeedbackList(userID:Long): List<SwingFeedback>
    suspend fun syncSwingFeedbackData(swingFeedbackSyncList: List<SwingFeedbackSync>): Result<CommonResponse<Unit>>
    suspend fun comparisonSwingFeedback(swingComparisonParam: SwingComparisonParam): Result<CommonResponse<List<SwingFeedbackDataNeedToUpload>>>
    suspend fun exportSwingFeedback(swingFeedbackExportParamList: List<SwingFeedbackExportImportParam>): Result<CommonResponse<Unit>>
    suspend fun importSwingFeedback(swingComparisonParam: SwingComparisonParam): Result<CommonResponse<List<SwingFeedbackExportImportParam>>>
    suspend fun getLastItem(userID: Long): SwingFeedback
    suspend fun getSwingDataSize(userID: Long): Int
}