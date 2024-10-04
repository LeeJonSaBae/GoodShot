package com.ijonsabae.domain.usecase.replay

interface UpdateUserIdUseCase {
    suspend operator fun invoke(oldUserId: Long, newUserId: Long) : Int
}