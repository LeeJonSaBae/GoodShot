package com.ijonsabae.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ijonsabae.data.datasource.local.ReplayLocalAllSwingFeedbackPagingSource
import com.ijonsabae.data.datasource.local.ReplayLocalLikeSwingFeedbackPagingSource
import com.ijonsabae.data.datasource.local.SwingFeedbackLocalDataSource
import com.ijonsabae.data.datasource.remote.SwingFeedbackRemoteDataSource
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.SwingComparisonParam
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.model.SwingFeedbackComment
import com.ijonsabae.domain.model.SwingFeedbackDataNeedToUpload
import com.ijonsabae.domain.model.SwingFeedbackExportImportParam
import com.ijonsabae.domain.model.SwingFeedbackSync
import com.ijonsabae.domain.model.SwingFeedbackSyncRoomData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val TAG = "SwingFeedbackRepositoryImpl_싸피"
class SwingFeedbackRepositoryImpl @Inject constructor(
    private val swingFeedbackLocalDataSource: SwingFeedbackLocalDataSource,
    private val swingFeedbackRemoteDataSource: SwingFeedbackRemoteDataSource,
    private val replayLocalAllSwingFeedbackPagingSource: ReplayLocalAllSwingFeedbackPagingSource,
    private val replayLocalLikeSwingFeedbackPagingSource: ReplayLocalLikeSwingFeedbackPagingSource
) : SwingFeedbackRepository {
    override suspend fun insertSwingFeedback(swingFeedback: SwingFeedback) {
        return swingFeedbackLocalDataSource.insertSwingFeedback(swingFeedback)
    }

    override suspend fun getSwingFeedback(userID: Long, swingCode: String): SwingFeedback {
        return swingFeedbackLocalDataSource.getSwingFeedback(userID, swingCode)
    }

    override fun getAllSwingFeedback(userID: Long): Flow<PagingData<SwingFeedback>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { replayLocalAllSwingFeedbackPagingSource }
        ).flow
    }

    override suspend fun getSwingFeedbackListNeedToUpload(userID:Long): List<SwingFeedback> {
        return swingFeedbackLocalDataSource.getSwingFeedbackListNeedToUpload(userID)
    }

    override fun getLikeSwingFeedbackList(userID: Long): Flow<PagingData<SwingFeedback>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { replayLocalLikeSwingFeedbackPagingSource }
        ).flow
    }

    override suspend fun deleteFeedback(userId: Long, swingCode: String): Int {
        return swingFeedbackLocalDataSource.deleteFeedback(userId, swingCode)
    }

    override suspend fun insertSwingFeedbackComment(swingFeedbackCommentEntity: SwingFeedbackComment) {
        return swingFeedbackLocalDataSource.insertSwingFeedbackComment(swingFeedbackCommentEntity)
    }

    override suspend fun getSwingFeedbackComment(
        userId: Long,
        swingCode: String
    ): List<SwingFeedbackComment> {
        return swingFeedbackLocalDataSource.getSwingFeedbackComment(userId, swingCode)
    }

    override suspend fun deleteFeedbackComment(userID: Long, swingCode: String): Int {
        return swingFeedbackLocalDataSource.deleteVideoComment(userID, swingCode)
    }

    override suspend fun updateUserId(oldUserId: Long, newUserId: Long): Int {
        return swingFeedbackLocalDataSource.updateUserId(oldUserId, newUserId)
    }

    override suspend fun updateLikeStatus(userID: Long, swingCode: String, likeStatus: Boolean, currentTime: Long): Int {
        return swingFeedbackLocalDataSource.updateLikeStatus(userID, swingCode, likeStatus, currentTime)
    }

    override suspend fun updateClampStatus(userID: Long, swingCode: String, clampStatus: Boolean): Int {
        return swingFeedbackLocalDataSource.updateClampStatus(userID, swingCode, clampStatus)
    }

    override suspend fun updateTitle(userID: Long, swingCode: String, title: String, currentTime: Long): Int {
        return swingFeedbackLocalDataSource.updateTitle(userID, swingCode, title, currentTime)
    }

    override suspend fun syncUpdateStatus(userID: Long): Int {
        return swingFeedbackLocalDataSource.syncUpdateStatus(userID)
    }

    override suspend fun hideSwingFeedback(userID: Long, swingCode: String, currentTime: Long): Int{
        return swingFeedbackLocalDataSource.hideSwingFeedback(userID, swingCode, currentTime)
    }

    override suspend fun getChangedSwingFeedback(userID: Long): List<SwingFeedbackSyncRoomData>{
        return swingFeedbackLocalDataSource.getChangedSwingFeedback(userID)
    }

    override suspend fun getAllSwingFeedbackList(userID: Long): List<SwingFeedback> {
        return swingFeedbackLocalDataSource.getAllSwingFeedbackList(userID)
    }

    override suspend fun syncSwingFeedbackData(swingFeedbackSyncList: List<SwingFeedbackSync>): Result<CommonResponse<Unit>> {
        return swingFeedbackRemoteDataSource.syncSwingFeedback(swingFeedbackSyncList)
    }

    override suspend fun comparisonSwingFeedback(swingComparisonParam: SwingComparisonParam): Result<CommonResponse<List<SwingFeedbackDataNeedToUpload>>> {
        return swingFeedbackRemoteDataSource.comparisonSwingFeedback(swingComparisonParam)
    }

    override suspend fun exportSwingFeedback(swingFeedbackExportParamList: List<SwingFeedbackExportImportParam>): Result<CommonResponse<Unit>> {
        return swingFeedbackRemoteDataSource.exportSwingFeedback(swingFeedbackExportParamList)
    }

    override suspend fun importSwingFeedback(swingComparisonParam: SwingComparisonParam): Result<CommonResponse<List<SwingFeedbackExportImportParam>>> {
        return swingFeedbackRemoteDataSource.importSwingFeedback(swingComparisonParam)
    }

    override suspend fun getLastItem(userID: Long): SwingFeedback{
        return swingFeedbackLocalDataSource.getLastItem(userID)
    }

    override suspend fun getSwingDataSize(userID: Long): Int {
        return swingFeedbackLocalDataSource.getSwingDataSize(userID)
    }
}