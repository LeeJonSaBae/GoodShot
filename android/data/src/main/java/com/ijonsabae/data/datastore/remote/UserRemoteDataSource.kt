package com.ijonsabae.data.datastore.remote

import com.ijonsabae.data.model.GenerateTemporaryPassWordParam
import com.ijonsabae.data.model.RequestEmailAuthCodeParam
import com.ijonsabae.data.retrofit.UserService
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.LoginParam
import com.ijonsabae.domain.model.RegisterParam
import com.ijonsabae.domain.model.Token
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(private val userService: UserService) {
    suspend fun login(loginParam: LoginParam): Result<CommonResponse<Token>>{
        return userService.login(loginParam)
    }

    suspend fun join(registerParam: RegisterParam): Result<CommonResponse<Unit>>{
        return userService.join(registerParam)
    }

    suspend fun requestEmailAuthCode(email: String): Result<CommonResponse<Unit>>{
        return userService.sendEmailAuthCode(RequestEmailAuthCodeParam(email))
    }

    suspend fun verifyEmailAuthCode(email: String, emailAuthCode: String): Result<CommonResponse<Boolean>>{
        return userService.checkEmailAuthCode(email, emailAuthCode)
    }

    suspend fun checkEmailDuplicated(email: String): Result<CommonResponse<Boolean>>{
        return userService.checkEmailDuplicated(email)
    }

    suspend fun generateTemporaryPassWord(name: String, email: String): Result<CommonResponse<Unit>>{
        return userService.generateTemporaryPassWord(GenerateTemporaryPassWordParam(
            name = name,
            email = email
        ))
    }
}