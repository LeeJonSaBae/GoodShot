package com.ijonsabae.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ijonsabae.data.datasource.local.SwingFeedbackLocalDataSource
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.model.SwingFeedbackComment
import com.ijonsabae.domain.model.SwingFeedbackSyncRoomData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SwingFeedbackRepositoryImpl @Inject constructor(
    private val swingFeedbackLocalDataSource: SwingFeedbackLocalDataSource
) : SwingFeedbackRepository {
    override fun insertSwingFeedback(swingFeedback: SwingFeedback) {
        return swingFeedbackLocalDataSource.insertSwingFeedback(swingFeedback)
    }

    override fun getSwingFeedback(userID: Long, swingCode: String): SwingFeedback {
        return swingFeedbackLocalDataSource.getSwingFeedback(userID, swingCode)
    }

    override fun getAllSwingFeedback(userID: Long): Flow<PagingData<SwingFeedback>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { swingFeedbackLocalDataSource.getAllSwingFeedback(userID = userID) }
        ).flow
    }

    override fun getLikeSwingFeedbackList(userID: Long): Flow<PagingData<SwingFeedback>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { swingFeedbackLocalDataSource.getLikeSwingFeedbackList(userID = userID) }
        ).flow
    }

    override fun deleteFeedback(userId: Long, swingCode: String): Int {
        return swingFeedbackLocalDataSource.deleteFeedback(userId, swingCode)
    }

    override fun insertSwingFeedbackComment(swingFeedbackCommentEntity: SwingFeedbackComment) {
        return swingFeedbackLocalDataSource.insertSwingFeedbackComment(swingFeedbackCommentEntity)
    }

    override fun getSwingFeedbackComment(
        userId: Long,
        swingCode: String
    ): List<SwingFeedbackComment> {
        return swingFeedbackLocalDataSource.getSwingFeedbackComment(userId, swingCode)
    }

    override fun deleteFeedbackComment(userID: Long, swingCode: String): Int {
        return swingFeedbackLocalDataSource.deleteVideoComment(userID, swingCode)
    }

    override fun updateUserId(oldUserId: Long, newUserId: Long): Int {
        return swingFeedbackLocalDataSource.updateUserId(oldUserId, newUserId)
    }

    override fun updateLikeStatus(userID: Long, swingCode: String, likeStatus: Boolean, currentTime: Long): Int {
        return swingFeedbackLocalDataSource.updateLikeStatus(userID, swingCode, likeStatus, currentTime)
    }

    override fun updateClampStatus(userID: Long, swingCode: String, clampStatus: Boolean): Int {
        return swingFeedbackLocalDataSource.updateClampStatus(userID, swingCode, clampStatus)
    }

    override fun updateTitle(userID: Long, swingCode: String, title: String, currentTime: Long): Int {
        return swingFeedbackLocalDataSource.updateTitle(userID, swingCode, title, currentTime)
    }

    override fun hideSwingFeedback(userID: Long, swingCode: String, currentTime: Long): Int{
        return swingFeedbackLocalDataSource.hideSwingFeedback(userID, swingCode, currentTime)
    }

    override fun getChangedSwingFeedback(userID: Long): List<SwingFeedbackSyncRoomData>{
        return swingFeedbackLocalDataSource.getChangedSwingFeedback(userID)
    }
}