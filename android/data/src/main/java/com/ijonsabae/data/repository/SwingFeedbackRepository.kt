package com.ijonsabae.data.repository

import androidx.paging.PagingData
import com.ijonsabae.domain.model.SwingFeedbackComment
import com.ijonsabae.domain.model.SwingFeedback
import kotlinx.coroutines.flow.Flow

interface SwingFeedbackRepository {
    fun insertSwingFeedback(swingFeedback: SwingFeedback)
    fun getSwingFeedback(userID: Long, swingCode: String): SwingFeedback
    fun getAllSwingFeedback(userID: Long): Flow<PagingData<SwingFeedback>>
    fun getLikeSwingFeedbackList(userID: Long): Flow<PagingData<SwingFeedback>>
    fun deleteFeedback(userId: Long, swingCode: String): Int
    fun insertSwingFeedbackComment(swingFeedbackCommentEntity: SwingFeedbackComment)
    fun getSwingFeedbackComment(userId: Long, swingCode: String): List<SwingFeedbackComment>
    fun deleteFeedbackComment(userID: Long, swingCode: String): Int
    fun updateUserId(oldUserId: Long, newUserId: Long): Int
    fun updateLikeStatus(userID: Long, swingCode: String, likeStatus: Boolean): Int
    fun updateClampStatus(userID: Long, swingCode: String, clampStatus: Boolean): Int
    fun updateTitle(userID: Long, swingCode: String, title: String): Int
}