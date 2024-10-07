package com.ijonsabae.domain.usecase.replay

interface UpdateUserIdUseCase {
    operator fun invoke(oldUserId: Long, newUserId: Long) : Int
}