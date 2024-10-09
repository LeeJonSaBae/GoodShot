package com.ijonsabae.domain.usecase.login

import com.ijonsabae.domain.model.CommonResponse

interface GetLocalUserNameUseCase {
    suspend operator fun invoke(): String?
}