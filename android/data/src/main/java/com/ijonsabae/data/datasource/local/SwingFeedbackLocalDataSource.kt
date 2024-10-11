package com.ijonsabae.data.datasource.local

import android.util.Log
import androidx.paging.PagingSource
import com.ijonsabae.data.dao.SwingFeedbackCommentDao
import com.ijonsabae.data.dao.SwingFeedbackDao
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.model.SwingFeedbackComment
import com.ijonsabae.domain.model.SwingFeedbackSyncRoomData
import javax.inject.Inject

private const val TAG = "SwingFeedbackLocalDataSource_싸피"
class SwingFeedbackLocalDataSource @Inject constructor(
    private val swingFeedbackDao: SwingFeedbackDao,
    private val swingFeedbackCommentDao: SwingFeedbackCommentDao
) {
    suspend fun insertSwingFeedback(swingFeedback: SwingFeedback){
        return swingFeedbackDao.insertSwingFeedback(swingFeedback)
    }
    suspend fun getSwingFeedback(userID: Long, swingCode: String): SwingFeedback {
        return swingFeedbackDao.getSwingFeedback(userID, swingCode)
    }
    fun getAllSwingFeedback(userID: Long): PagingSource<Int, SwingFeedback> {
        return swingFeedbackDao.getAllSwingFeedback(userID)
    }
    suspend fun getSwingFeedbackListNeedToUpload(userID:Long): List<SwingFeedback>{
        return swingFeedbackDao.getAllSwingFeedbackNeedToUpload(userID)
    }
    suspend fun getSwingDataSize(userID: Long): Int{
        return swingFeedbackDao.getSwingDataSize(userID)
    }

    suspend fun getLastItem(userID: Long): SwingFeedback{
        return swingFeedbackDao.getLastItem(userID)
    }

    suspend fun getAllSwingFeedbackList(userID:Long): List<SwingFeedback>{
        return swingFeedbackDao.getAllSwingFeedbackList(userID)
    }
    fun getLikeSwingFeedbackList(userID: Long): PagingSource<Int, SwingFeedback> {
        return swingFeedbackDao.getLikeSwingFeedback(userID)
    }

    suspend fun deleteFeedback(userId: Long, swingCode: String): Int{
        return swingFeedbackDao.deleteFeedback(userId, swingCode)
    }
    suspend fun insertSwingFeedbackComment(swingFeedbackCommentEntity: SwingFeedbackComment){
        return swingFeedbackCommentDao.insertSwingFeedbackComment(swingFeedbackCommentEntity)
    }
    suspend fun getSwingFeedbackComment(userId: Long, swingCode: String): List<SwingFeedbackComment>{
        return swingFeedbackCommentDao.getSwingFeedbackComment(userId, swingCode)
    }
    suspend fun deleteVideoComment(userID: Long, swingCode: String): Int {
        return swingFeedbackCommentDao.deleteVideoComment(userID, swingCode)
    }
    suspend fun updateUserId(oldUserId: Long, newUserId: Long): Int{
        swingFeedbackDao.updateUserId(oldUserId, newUserId)
        return swingFeedbackCommentDao.updateUserId(oldUserId, newUserId)
    }
    suspend fun updateLikeStatus(userID: Long, swingCode: String, likeStatus: Boolean, currentTime: Long): Int{
        return swingFeedbackDao.updateLikeStatus(userID, swingCode, likeStatus, currentTime)
    }
    fun updateClampStatus(userID: Long, swingCode: String, clampStatus: Boolean): Int{
        return swingFeedbackDao.updateClampStatus(userID, swingCode, clampStatus)
    }
    suspend fun updateTitle(userID: Long, swingCode: String, title: String, currentTime: Long): Int{
        return swingFeedbackDao.updateTitle(userID, swingCode, title, currentTime)
    }
    suspend fun syncUpdateStatus(userID: Long): Int{
        return swingFeedbackDao.syncUpdateStatus(userID)
    }
    suspend fun hideSwingFeedback(userID: Long, swingCode: String, currentTime: Long): Int{
        return swingFeedbackDao.hideSwingFeedback(userID,swingCode, currentTime)
    }
    suspend fun getChangedSwingFeedback(userID: Long): List<SwingFeedbackSyncRoomData>{
        return swingFeedbackDao.getChangedSwingFeedback(userID)
    }
}
