package com.ijonsabae.domain.usecase.login

interface GetLocalRefreshTokenUseCase {
    suspend operator fun invoke() : String?
}