package com.ijonsabae.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "swing_feedback_comment",
    primaryKeys = ["userID", "swingCode", "poseType", "content", "commentType"],
    foreignKeys = [ForeignKey(
        entity = SwingFeedback::class,
        parentColumns = [
        "userID",
        "swingCode",
        ],
        childColumns = [
            "userID",
            "swingCode",
        ],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )]
    )
data class SwingFeedbackComment(
    val userID: Long,
    val swingCode: String,
    val poseType: Int,
    val content: String,
    val commentType: Int
)
