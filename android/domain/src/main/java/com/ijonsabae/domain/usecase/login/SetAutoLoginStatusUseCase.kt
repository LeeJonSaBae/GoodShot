package com.ijonsabae.domain.usecase.login

interface SetAutoLoginStatusUseCase {
    suspend operator fun invoke(autoLogin: Boolean): Unit
}