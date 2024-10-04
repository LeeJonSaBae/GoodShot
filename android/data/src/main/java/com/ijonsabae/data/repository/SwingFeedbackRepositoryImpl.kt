package com.ijonsabae.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ijonsabae.data.datasource.local.SwingFeedbackLocalDataSource
import com.ijonsabae.domain.model.SwingFeedbackComment
import com.ijonsabae.domain.model.SwingFeedback
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SwingFeedbackRepositoryImpl @Inject constructor(
    private val swingFeedbackLocalDataSource: SwingFeedbackLocalDataSource
) : SwingFeedbackRepository {
    override fun insertSwingFeedback(swingFeedback: SwingFeedback) {
        return swingFeedbackLocalDataSource.insertSwingFeedback(swingFeedback)
    }

    override fun getSwingFeedback(userID: Long, videoName: String): SwingFeedback {
        return swingFeedbackLocalDataSource.getSwingFeedback(userID, videoName)
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

    override fun deleteFeedback(userId: Long, videoName: String): Int {
        return swingFeedbackLocalDataSource.deleteFeedback(userId, videoName)
    }

    override fun insertSwingFeedbackComment(swingFeedbackCommentEntity: SwingFeedbackComment) {
        return swingFeedbackLocalDataSource.insertSwingFeedbackComment(swingFeedbackCommentEntity)
    }

    override fun getSwingFeedbackComment(
        userId: Long,
        videoName: String
    ): SwingFeedbackComment {
        return swingFeedbackLocalDataSource.getSwingFeedbackComment(userId, videoName)
    }

    override fun deleteFeedbackComment(userID: Long, videoName: String): Int {
        return swingFeedbackLocalDataSource.deleteVideoComment(userID, videoName)
    }

    override fun updateUserId(oldUserId: Long, newUserId: Long): Int {
        return swingFeedbackLocalDataSource.updateUserId(oldUserId, newUserId)
    }
}