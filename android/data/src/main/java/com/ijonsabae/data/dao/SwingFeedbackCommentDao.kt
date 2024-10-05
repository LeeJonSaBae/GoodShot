package com.ijonsabae.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ijonsabae.domain.model.SwingFeedbackComment

@Dao
interface SwingFeedbackCommentDao {
    @Insert
    fun insertSwingFeedbackComment(swingFeedbackComment: SwingFeedbackComment)

    @Query("SELECT * FROM swing_feedback_comment WHERE userID = :userId AND swingCode = :videoName")
    fun getSwingFeedbackComment(userId: Long, videoName: String): SwingFeedbackComment

    @Query("DELETE FROM swing_feedback_comment WHERE userID = :userId AND swingCode = :videoName")
    fun deleteVideoComment(userId: Long, videoName: String): Int

    @Query("UPDATE swing_feedback_comment SET userID = :newUserId WHERE userID = :oldUserId")
    fun updateUserId(oldUserId: Long, newUserId: Long): Int
}