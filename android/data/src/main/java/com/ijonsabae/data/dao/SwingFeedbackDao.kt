package com.ijonsabae.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.model.SwingFeedbackSyncRoomData

@Dao
interface SwingFeedbackDao {
    @Insert
    suspend fun insertSwingFeedback(swingFeedback: SwingFeedback)

    @Query("SELECT * FROM swing_feedback WHERE userID == :userId AND swingCode == :swingCode")
    suspend fun getSwingFeedback(userId: Long, swingCode: String): SwingFeedback

    @Query("SELECT * FROM swing_feedback WHERE userID == :userID and isUpdated != 2 order by createdAt DESC" )
    fun getAllSwingFeedback(userID: Long): PagingSource<Int,SwingFeedback>

    @Query("SELECT * FROM swing_feedback WHERE isUpdated == 0 and userID == :userID")
    suspend fun getAllSwingFeedbackNeedToUpload(userID:Long): List<SwingFeedback>

    @Query("SELECT * FROM swing_feedback WHERE isUpdated != 2 and userID == :userID")
    suspend fun getAllSwingFeedbackList(userID:Long): List<SwingFeedback>

    @Query("SELECT likeStatus, title, swingCode, date, isUpdated FROM swing_feedback WHERE userID == :userID and isUpdated != 0")
    suspend fun getChangedSwingFeedback(userID: Long): List<SwingFeedbackSyncRoomData>

    @Query("SELECT * FROM swing_feedback where userID=:userID and likeStatus== 1 and isUpdated != 2 order by createdAt DESC")
    fun getLikeSwingFeedback(userID: Long): PagingSource<Int,SwingFeedback>

    @Query("SELECT COUNT(*) FROM swing_feedback where userID==:userID and isUpdated != 2")
    suspend fun getSwingDataSize(userID: Long): Int

    @Query("SELECT * FROM swing_feedback where userID==:userID and isUpdated!= 2 order by createdAt DESC  LIMIT 1")
    suspend fun getLastItem(userID: Long): SwingFeedback

    @Query("DELETE FROM swing_feedback WHERE userID == :userId AND swingCode == :swingCode")
    suspend fun deleteFeedback(userId: Long, swingCode: String): Int

    @Query("UPDATE swing_feedback SET userID = :newUserId WHERE userID == :oldUserId")
    suspend fun updateUserId(oldUserId: Long, newUserId: Long): Int

    @Query("UPDATE swing_feedback SET likeStatus = :likeStatus, isUpdated = 1, date = :currentTime WHERE userID == :userID and swingCode == :swingCode")
    suspend fun updateLikeStatus(userID: Long, swingCode: String, likeStatus: Boolean, currentTime: Long): Int

    @Query("UPDATE swing_feedback SET isClamped = :clampStatus WHERE userID == :userID and swingCode == :swingCode")
    fun updateClampStatus(userID: Long, swingCode: String, clampStatus: Boolean): Int

    @Query("UPDATE swing_feedback SET title = :title, isUpdated = 1, date = :currentTime WHERE userID == :userID and swingCode == :swingCode")
    suspend fun updateTitle(userID: Long, swingCode: String, title: String, currentTime: Long): Int

    @Query("UPDATE swing_feedback SET isUpdated = 0 WHERE userID == :userID and isUpdated != 2")
    suspend fun syncUpdateStatus(userID: Long): Int

    @Query("UPDATE swing_feedback SET isUpdated = 2, date = :currentTime WHERE userID == :userID and swingCode == :swingCode")
    suspend fun hideSwingFeedback(userID: Long, swingCode: String, currentTime: Long): Int
}