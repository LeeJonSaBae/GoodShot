package com.ijonsabae.domain.usecase.login

import com.ijonsabae.domain.model.CommonResponse

interface GetRemoteUserNameUseCase {
    suspend operator fun invoke(): Result<CommonResponse<String>>
}