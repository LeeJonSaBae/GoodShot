package com.ijonsabae.data.datasource.local

import androidx.paging.PagingSource
import com.ijonsabae.data.dao.SwingFeedbackCommentDao
import com.ijonsabae.data.dao.SwingFeedbackDao
import com.ijonsabae.domain.model.SwingFeedbackComment
import com.ijonsabae.domain.model.SwingFeedback
import javax.inject.Inject

class SwingFeedbackLocalDataSource @Inject constructor(
    private val swingFeedbackDao: SwingFeedbackDao,
    private val swingFeedbackCommentDao: SwingFeedbackCommentDao
) {
    fun insertSwingFeedback(swingFeedback: SwingFeedback){
        return swingFeedbackDao.insertSwingFeedback(swingFeedback)
    }
    fun getSwingFeedback(userID: Long, swingCode: String): SwingFeedback {
        return swingFeedbackDao.getSwingFeedback(userID, swingCode)
    }
    fun getAllSwingFeedback(userID: Long): PagingSource<Int, SwingFeedback> {
        return swingFeedbackDao.getAllSwingFeedback(userID)
    }
    fun getLikeSwingFeedbackList(userID: Long): PagingSource<Int, SwingFeedback> {
        return swingFeedbackDao.getLikeSwingFeedback(userID)
    }
    fun deleteFeedback(userId: Long, swingCode: String): Int{
        return swingFeedbackDao.deleteFeedback(userId, swingCode)
    }
    fun insertSwingFeedbackComment(swingFeedbackCommentEntity: SwingFeedbackComment){
        return swingFeedbackCommentDao.insertSwingFeedbackComment(swingFeedbackCommentEntity)
    }
    fun getSwingFeedbackComment(userId: Long, swingCode: String): SwingFeedbackComment{
        return swingFeedbackCommentDao.getSwingFeedbackComment(userId, swingCode)
    }
    fun deleteVideoComment(userID: Long, swingCode: String): Int {
        return swingFeedbackCommentDao.deleteVideoComment(userID, swingCode)
    }
    fun updateUserId(oldUserId: Long, newUserId: Long): Int{
        swingFeedbackDao.updateUserId(oldUserId, newUserId)
        return swingFeedbackCommentDao.updateUserId(oldUserId, newUserId)
    }

    fun updateLikeStatus(userID: Long, swingCode: String, likeStatus: Boolean): Int{
        return swingFeedbackDao.updateLikeStatus(userID, swingCode, likeStatus)
    }

    fun updateClampStatus(userID: Long, swingCode: String, clampStatus: Boolean): Int{
        return swingFeedbackDao.updateClampStatus(userID, swingCode, clampStatus)
    }

    fun updateTitle(userID: Long, swingCode: String, title: String): Int{
        return swingFeedbackDao.updateTitle(userID, swingCode, title)
    }
}
