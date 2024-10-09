package com.ijonsabae.domain.usecase.home

interface GetSwingDataSizeUseCase {
    suspend operator fun invoke(userID: Long): Int
}