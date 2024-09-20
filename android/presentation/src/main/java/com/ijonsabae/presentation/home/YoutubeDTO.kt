package com.ijonsabae.presentation.home

data class YoutubeDTO(
    val title: String,
    val thumbnail: String?,
    val originallink: String?,
    val link: String?,
    val pubDate: String?,
    var isVisible: Boolean = false
)