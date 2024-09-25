package com.ijonsabae.domain.model

data class FeedBack(
    val down: Float,
    val tempo: Float,
    val back: Float,
    val feedBackProblem: String,
    val feedBackSolution: String,
    val feedBackCheckListTitle: String,
    val feedBackCheckList: List<String>
)
