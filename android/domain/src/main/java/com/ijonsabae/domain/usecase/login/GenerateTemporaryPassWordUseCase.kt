package com.ijonsabae.domain.usecase.login

import com.ijonsabae.domain.model.CommonResponse

interface GenerateTemporaryPassWordUseCase {
    suspend operator fun invoke(name: String, email: String): Result<CommonResponse<Unit>>
}