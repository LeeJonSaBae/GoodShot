package com.ijonsabae.domain.usecase.login

interface GetLocalAccessTokenUseCase {
    suspend operator fun invoke() : String?
}