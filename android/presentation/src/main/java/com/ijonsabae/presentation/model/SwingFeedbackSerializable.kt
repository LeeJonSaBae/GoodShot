package com.ijonsabae.presentation.model

import com.ijonsabae.domain.model.Similarity
import kotlinx.serialization.Serializable

@Serializable
data class SwingFeedbackSerializable(
    val userID: Long,
    val swingCode: String,
    val likeStatus: Boolean = false,
    val similarity: Similarity, // 포즈별 유사도를 String으로 저장
    val solution: String,
    val score: Int,
    val tempo: Double,
    val title: String,
    val date: Long,
    val isClamped: Boolean = false
): java.io.Serializable
