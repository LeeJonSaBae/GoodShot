package com.ijonsabae.domain.usecase.replay

interface UpdateClampStatusUseCase {
    suspend operator fun invoke(userID: Long, swingCode: String, clampStatus: Boolean) : Int
}