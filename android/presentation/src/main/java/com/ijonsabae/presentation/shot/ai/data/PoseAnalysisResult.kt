package com.ijonsabae.presentation.shot.ai.data

data class PoseAnalysisResult(
    val backSwingFeedbacks: List<Feedback>,
    val downSwingFeedbacks: List<Feedback>
)