package com.ijonsabae.presentation.shot.ai.data

data class PoseAnalysisResult(
    val solution: Solution,
    val backSwingFeedbacks: List<Comment>,
    val downSwingFeedbacks: List<Comment>
)