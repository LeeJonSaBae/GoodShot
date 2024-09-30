package com.ijonsabae.domain.usecase.login

import com.ijonsabae.domain.model.CommonResponse

interface CheckEmailDuplicatedUseCase {
    suspend operator fun invoke(email: String): Result<CommonResponse<Boolean>>
}