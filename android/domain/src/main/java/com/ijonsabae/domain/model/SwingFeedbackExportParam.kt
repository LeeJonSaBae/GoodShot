package com.ijonsabae.domain.model

data class SwingFeedbackExportParam(
    val id: Long,
    val backSwingComments: List<SwingCommentExportParam>,
    val downSwingComments: List<SwingCommentExportParam>,
    val similarity: Similarity,
    val solution: String,
    val score: Int,
    val tempo: Double,
    val likeStatus: Boolean,
    val title: String,
    val code: String,
    val time: String,
)