package com.ijonsabae.domain.usecase.replay

interface SyncUpdateStatusUseCase {
    operator fun invoke(userID: Long): Int
}