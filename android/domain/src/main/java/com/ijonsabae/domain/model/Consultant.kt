package com.ijonsabae.domain.model

data class Consultant(
    val name: String,
    val profileImage: String,
    val career: Int,
    val pro: String,
    val phoneNumber: String,
    val certification: List<String>,
    val chatUrl: String
)
