package com.ijonsabae.domain.usecase.login

interface GetAutoLoginStatusUseCase {
    suspend operator fun invoke(): Boolean
}