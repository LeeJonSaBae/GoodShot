package com.ijonsabae.domain.model

data class FeedBack(
    val down: String,
    val tempo: String,
    val back: String,
    val feedBackSolution: String,
    val feedBackCheckListTitle: String,
    val feedBackCheckList: List<String>,
    val userSwingImage: ByteArray,
    val expertSwingImageResId: Int
)