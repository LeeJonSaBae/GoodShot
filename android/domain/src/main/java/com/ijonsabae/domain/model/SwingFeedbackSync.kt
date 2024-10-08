package com.ijonsabae.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SwingFeedbackSync(
    val likeStatus: Boolean,
    val title: String,
    val code: String,
    val time: String,
    val update: Int
)
