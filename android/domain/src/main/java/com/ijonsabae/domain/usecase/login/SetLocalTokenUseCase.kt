package com.ijonsabae.domain.usecase.login

import com.ijonsabae.domain.model.Token

interface SetLocalTokenUseCase {
    suspend operator fun invoke(token: Token)
}