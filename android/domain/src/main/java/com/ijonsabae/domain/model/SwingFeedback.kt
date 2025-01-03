package com.ijonsabae.domain.model

import androidx.room.Entity

@Entity(tableName = "swing_feedback", primaryKeys = ["userID", "swingCode"])
data class SwingFeedback(
    val userID: Long,
    val swingCode: String,
    val likeStatus: Boolean = false,
    val similarity: Similarity, // 포즈별 유사도를 String으로 저장
    val solution: String,
    val score: Int,
    val tempo: Double,
    val title: String,
    val date: Long,
    val createdAt: Long,
    val isClamped: Boolean = false,
    val isUpdated: Int = 0
)
//TODO(스윙 피드백 생성하는 곳에 적용하기)