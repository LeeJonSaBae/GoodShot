package com.ijonsabae.domain.repository

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.LoginParam
import com.ijonsabae.domain.model.RegisterParam
import com.ijonsabae.domain.model.Token

interface UserRepository {
    suspend fun login(loginParam: LoginParam): Result<CommonResponse<Token>>
    suspend fun join(registerParam: RegisterParam): Result<CommonResponse<Unit>>
    suspend fun requestEmailAuthCode(email: String): Result<CommonResponse<Unit>>
    suspend fun verifyEmailAuthCode(email: String, emailAuthCode: String): Result<CommonResponse<Boolean>>
    suspend fun checkEmailDuplicated(email: String): Result<CommonResponse<Boolean>>
    suspend fun generateTemporaryPassWord(name: String, email: String): Result<CommonResponse<Unit>>
}