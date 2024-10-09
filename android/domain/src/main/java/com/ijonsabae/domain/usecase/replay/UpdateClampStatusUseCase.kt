package com.ijonsabae.domain.usecase.replay

interface UpdateClampStatusUseCase {
    operator fun invoke(userID: Long, swingCode: String, clampStatus: Boolean) : Int
}