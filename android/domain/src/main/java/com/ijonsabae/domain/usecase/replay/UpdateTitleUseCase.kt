package com.ijonsabae.domain.usecase.replay

interface UpdateTitleUseCase {
    suspend operator fun invoke(userID: Long, swingCode: String, title: String, currentTime: Long) : Int
}