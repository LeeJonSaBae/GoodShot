package com.ijonsabae.data.repository

import com.ijonsabae.data.datasource.local.UserLocalDataSource
import com.ijonsabae.data.datasource.remote.UserRemoteDataSource
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.LoginParam
import com.ijonsabae.domain.model.RegisterParam
import com.ijonsabae.domain.model.Token
import com.ijonsabae.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource
): UserRepository {
    override suspend fun login(loginParam: LoginParam): Result<CommonResponse<Token>> {
        return userRemoteDataSource.login(loginParam)
    }

    override suspend fun logout(): Result<CommonResponse<Unit>> {
        return userRemoteDataSource.logout()
    }

    override suspend fun join(registerParam: RegisterParam): Result<CommonResponse<Unit>> {
        return userRemoteDataSource.join(registerParam)
    }

    override suspend fun resign(): Result<CommonResponse<Unit>> {
        return userRemoteDataSource.resign()
    }

    override suspend fun requestEmailAuthCode(email: String): Result<CommonResponse<Unit>>{
        return userRemoteDataSource.requestEmailAuthCode(email)
    }

    override suspend fun verifyEmailAuthCode(email: String, emailAuthCode: String): Result<CommonResponse<Boolean>>{
        return userRemoteDataSource.verifyEmailAuthCode(email, emailAuthCode)
    }

    override suspend fun checkEmailDuplicated(email: String): Result<CommonResponse<Boolean>>{
        return userRemoteDataSource.checkEmailDuplicated(email)
    }

    override suspend fun generateTemporaryPassWord(
        name: String,
        email: String
    ): Result<CommonResponse<Unit>> {
        return userRemoteDataSource.generateTemporaryPassWord(name, email)
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): Result<CommonResponse<Unit>>{
        return userRemoteDataSource.changePassword(oldPassword, newPassword)
    }

    override suspend fun getAutoLoginStatus(): Boolean {
        return userLocalDataSource.getAutoLoginStatus()
    }

    override suspend fun setAutoLoginStatus(autoLogin: Boolean) {
        return userLocalDataSource.setAutoLoginStatus(autoLogin)
    }
}