package com.ijonsabae.domain.usecase.login

import com.ijonsabae.domain.model.CheckCode
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.RegisterParam

interface VerifyEmailAuthCodeUseCase {
    suspend operator fun invoke(email: String, emailAuthCode: String): Result<CommonResponse<CheckCode>>
}