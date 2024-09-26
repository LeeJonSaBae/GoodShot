package com.ijonsabae.domain.usecase.login

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.LoginParam
import com.ijonsabae.domain.model.Token

interface LoginUseCase {
    suspend operator fun invoke(loginParam: LoginParam) : CommonResponse<Token>
}