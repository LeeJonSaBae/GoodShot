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
    fun getSwingFeedback(userID: Long, videoName: String): SwingFeedback {
        return swingFeedbackDao.getSwingFeedback(userID, videoName)
    }
    fun getAllSwingFeedback(userID: Long): PagingSource<Int, SwingFeedback> {
        return swingFeedbackDao.getAllSwingFeedback(userID)
    }
    fun getLikeSwingFeedbackList(userID: Long): PagingSource<Int, SwingFeedback> {
        return swingFeedbackDao.getLikeSwingFeedback(userID)
    }
    fun deleteFeedback(userId: Long, videoName: String): Int{
        return swingFeedbackDao.deleteFeedback(userId, videoName)
    }
    fun insertSwingFeedbackComment(swingFeedbackCommentEntity: SwingFeedbackComment){
        return swingFeedbackCommentDao.insertSwingFeedbackComment(swingFeedbackCommentEntity)
    }
    fun getSwingFeedbackComment(userId: Long, videoName: String): SwingFeedbackComment{
        return swingFeedbackCommentDao.getSwingFeedbackComment(userId, videoName)
    }
    fun deleteVideoComment(userID: Long, videoName: String): Int {
        return swingFeedbackCommentDao.deleteVideoComment(userID, videoName)
    }
    fun updateUserId(oldUserId: Long, newUserId: Long): Int{
        swingFeedbackDao.updateUserId(oldUserId, newUserId)
        return swingFeedbackCommentDao.updateUserId(oldUserId, newUserId)
    }
}
