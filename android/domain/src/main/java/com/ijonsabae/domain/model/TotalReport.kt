package com.ijonsabae.domain.model

data class TotalReport(
    val content: String,
    val name: String,
    val problems: List<String>,
    val score: Double,
    val similarity: List<Int>
)