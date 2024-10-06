package com.ijonsabae.domain.usecase.replay

interface UpdateTitleUseCase {
    operator fun invoke(userID: Long, swingCode: String, title: String) : Int
}