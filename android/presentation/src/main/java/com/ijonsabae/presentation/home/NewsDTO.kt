package com.ijonsabae.presentation.home

data class NewsDTO(
    val title: String,
    val originallink: String?,
    val link: String?,
    val description: String,
    val pubDate: String?,
    val mountainName: String?,
    val mountainImg: String?,
)