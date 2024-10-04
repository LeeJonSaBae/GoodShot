package com.ijonsabae.domain.usecase.login

interface GetUserIdUseCase {
    suspend operator fun invoke(): Long
}