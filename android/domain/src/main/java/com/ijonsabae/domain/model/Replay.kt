package com.ijonsabae.domain.model

data class Replay(
    val thumbnail: String, // 영상 썸네일 이미지
    val title: String,
    val date: String,
    val like: Boolean, // 즐겨찾기
    val score: Int, // 점수
    val tempo: Double, // 템포
    var isClamped: Boolean = false
)
