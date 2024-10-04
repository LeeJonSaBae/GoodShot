package com.ijonsabae.data.repository

import androidx.paging.PagingData
import com.ijonsabae.domain.model.SwingFeedbackComment
import com.ijonsabae.domain.model.SwingFeedback
import kotlinx.coroutines.flow.Flow

interface SwingFeedbackRepository {
    fun insertSwingFeedback(swingFeedback: SwingFeedback)
    fun getSwingFeedback(userID: Long, videoName: String): SwingFeedback
    fun getAllSwingFeedback(userID: Long): Flow<PagingData<SwingFeedback>>
    fun getLikeSwingFeedbackList(userID: Long): Flow<PagingData<SwingFeedback>>
    fun deleteFeedback(userId: Long, videoName: String): Int
    fun insertSwingFeedbackComment(swingFeedbackCommentEntity: SwingFeedbackComment)
    fun getSwingFeedbackComment(userId: Long, videoName: String): SwingFeedbackComment
    fun deleteFeedbackComment(userID: Long, videoName: String): Int
    fun updateUserId(oldUserId: Long, newUserId: Long): Int
}