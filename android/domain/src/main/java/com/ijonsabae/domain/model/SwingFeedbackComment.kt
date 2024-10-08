package com.ijonsabae.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "swing_feedback_comment",
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
    @PrimaryKey(autoGenerate = true)
    val id: Long=0,
    val userID: Long,
    val swingCode: String,
    val poseType: Int,
    val content: String,
    val commentType: Int
)
