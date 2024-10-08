package com.ijonsabae.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ijonsabae.domain.model.SwingFeedbackComment

@Dao
interface SwingFeedbackCommentDao {
    @Insert
    fun insertSwingFeedbackComment(swingFeedbackComment: SwingFeedbackComment)

    @Query("SELECT * FROM swing_feedback_comment WHERE userID = :userId AND swingCode = :swingCode")
    fun getSwingFeedbackComment(userId: Long, swingCode: String): List<SwingFeedbackComment>

    @Query("DELETE FROM swing_feedback_comment WHERE userID = :userId AND swingCode = :swingCode")
    fun deleteVideoComment(userId: Long, swingCode: String): Int

    @Query("UPDATE swing_feedback_comment SET userID = :newUserId WHERE userID = :oldUserId")
    fun updateUserId(oldUserId: Long, newUserId: Long): Int
}