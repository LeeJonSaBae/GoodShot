package com.ijonsabae.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ijonsabae.domain.model.SwingFeedback

@Dao
interface SwingFeedbackDao {
    @Insert
    fun insertSwingFeedback(swingFeedback: SwingFeedback)

    @Query("SELECT * FROM swing_feedback WHERE userID = :userId AND videoName = :videoName")
    fun getSwingFeedback(userId: Long, videoName: String): SwingFeedback

    @Query("SELECT * FROM swing_feedback WHERE userID = :userID")
    fun getAllSwingFeedback(userID: Long): PagingSource<Int,SwingFeedback>

    @Query("SELECT * FROM swing_feedback where userID=:userID and likeStatus=1")
    fun getLikeSwingFeedback(userID: Long): PagingSource<Int,SwingFeedback>

    @Query("DELETE FROM swing_feedback WHERE userID = :userId AND videoName = :videoName")
    fun deleteFeedback(userId: Long, videoName: String): Int

    @Query("UPDATE swing_feedback SET userID = :newUserId WHERE userID = :oldUserId")
    fun updateUserId(oldUserId: Long, newUserId: Long): Int
}