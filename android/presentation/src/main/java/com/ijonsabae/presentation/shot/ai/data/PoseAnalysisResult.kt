package com.ijonsabae.presentation.shot.ai.data

data class PoseAnalysisResult(
    val solution: Solution,
    val backSwingProblems: List<Comment>,
    val downSwingProblems: List<Comment>
)