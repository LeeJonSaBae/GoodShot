package com.ijonsabae.domain.model

import androidx.room.Entity

@Entity(tableName = "swing_feedback", primaryKeys = ["userID", "videoName"])
data class SwingFeedback(
    val userID: Long,
    val videoName: String,
    val likeStatus: Boolean = false,
    val similarity: Similarity, // 포즈별 유사도를 String으로 저장
    val solution: String,
    val score: Int,
    val tempo: Double,
    val title: String,
    val date: Long
)
