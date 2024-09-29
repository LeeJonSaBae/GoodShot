package com.ijonsabae.domain.usecase.login

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.RegisterParam

interface RegisterUseCase {
    suspend operator fun invoke(registerParam: RegisterParam): Result<CommonResponse<Unit>>
}