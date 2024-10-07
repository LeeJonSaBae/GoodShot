package com.ijonsabae.domain.model

data class SwingFeedbackDataNeedToUpload(
    val code: String,
    val presignedUrls: List<String>
)
