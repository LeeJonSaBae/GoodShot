package com.ijonsabae.domain.model

data class Replay(
    val thumbnail: String, // 영상 썸네일 이미지
    val title: String,
    val date: String,
    val swingPose: String, // 정면, 측면
    val golfClub: String, // 아이언, 드라이버
    val like: Boolean, // 즐겨찾기
    var isClamped: Boolean = false
)
