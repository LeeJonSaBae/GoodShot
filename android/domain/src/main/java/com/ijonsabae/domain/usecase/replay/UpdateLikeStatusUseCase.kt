package com.ijonsabae.domain.usecase.replay

interface UpdateLikeStatusUseCase {
    operator fun invoke(userID: Long, swingCode: String, likeStatus: Boolean, currentTime: Long) : Int
}