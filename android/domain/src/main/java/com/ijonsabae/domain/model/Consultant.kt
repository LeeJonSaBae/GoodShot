package com.ijonsabae.domain.model

data class Consultant(
    val name: String,
    val profileImage: String,
    val career: Int,
    val course: String,
    val expertise: String,
    val certification: List<String>,
    val topic: List<String>
)
