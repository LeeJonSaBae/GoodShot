package com.ijonsabae.domain.model

data class SwingFeedbackExportImportParam(
    val id: Long,
    val backSwingComments: List<SwingCommentExportImportParam>,
    val downSwingComments: List<SwingCommentExportImportParam>,
    val similarity: Similarity,
    val solution: String,
    val score: Int,
    val tempo: Double,
    val likeStatus: Boolean,
    val title: String,
    val code: String,
    val time: String,
)