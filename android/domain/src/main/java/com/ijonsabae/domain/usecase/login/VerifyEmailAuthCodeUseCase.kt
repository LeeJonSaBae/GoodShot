package com.ijonsabae.domain.usecase.login

import com.ijonsabae.domain.model.CommonResponse

interface VerifyEmailAuthCodeUseCase {
    suspend operator fun invoke(email: String, emailAuthCode: String): Result<CommonResponse<Boolean>>
}