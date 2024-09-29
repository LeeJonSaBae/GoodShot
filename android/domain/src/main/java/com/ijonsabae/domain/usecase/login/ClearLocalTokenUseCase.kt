package com.ijonsabae.domain.usecase.login

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Token

interface ClearLocalTokenUseCase {
    suspend operator fun invoke()
}