package com.ijonsabae.domain.model

import androidx.room.Entity
import kotlinx.serialization.json.JsonNull.content

@Entity(tableName = "swing_feedback_comment", primaryKeys = ["userID", "videoName", "poseType", "content", "commentType"])
data class SwingFeedbackComment(
    val userID: Long,
    val videoName: String,
    val poseType: String,
    val content: String,
    val commentType: String
)
