package com.ijonsabae.domain.usecase.login

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.RegisterParam

interface RequestEmailAuthCodeUseCase{
    suspend operator fun invoke(email: String): Result<CommonResponse<Unit>>
}