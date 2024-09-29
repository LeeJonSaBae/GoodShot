package com.ijonsabae.domain.usecase.login

interface GetLocalAccessTokenCreatedTimeUseCase {
    suspend operator fun invoke() : Long?
}