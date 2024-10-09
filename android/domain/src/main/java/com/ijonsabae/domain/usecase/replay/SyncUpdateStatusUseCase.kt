package com.ijonsabae.domain.usecase.replay

interface SyncUpdateStatusUseCase {
    suspend operator fun invoke(userID: Long): Int
}