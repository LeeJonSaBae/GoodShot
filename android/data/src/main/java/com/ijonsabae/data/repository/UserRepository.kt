package com.ijonsabae.data.repository

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.LoginParam
import com.ijonsabae.domain.model.RegisterParam
import com.ijonsabae.domain.model.Token

interface UserRepository {
    suspend fun login(loginParam: LoginParam): Result<CommonResponse<Token>>
    suspend fun logout(): Result<CommonResponse<Unit>>
    suspend fun join(registerParam: RegisterParam): Result<CommonResponse<Unit>>
    suspend fun resign(): Result<CommonResponse<Unit>>
    suspend fun requestEmailAuthCode(email: String): Result<CommonResponse<Unit>>
    suspend fun verifyEmailAuthCode(email: String, emailAuthCode: String): Result<CommonResponse<Boolean>>
    suspend fun checkEmailDuplicated(email: String): Result<CommonResponse<Boolean>>
    suspend fun generateTemporaryPassWord(name: String, email: String): Result<CommonResponse<Unit>>
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<CommonResponse<Unit>>
    suspend fun getAutoLoginStatus(): Boolean
    suspend fun setAutoLoginStatus(autoLogin: Boolean)
    suspend fun getRemoteUsername(): Result<CommonResponse<String>>
    suspend fun getLocalUsername(): String?
    suspend fun setLocalUserName(name: String)
    suspend fun clearUserName()
}