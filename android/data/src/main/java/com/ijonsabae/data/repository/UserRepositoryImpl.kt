package com.ijonsabae.data.repository

import com.ijonsabae.data.datastore.remote.UserRemoteDataSource
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.LoginParam
import com.ijonsabae.domain.model.RegisterParam
import com.ijonsabae.domain.model.Token
import com.ijonsabae.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val userRemoteDataSource: UserRemoteDataSource): UserRepository {
    override suspend fun login(loginParam: LoginParam): Result<CommonResponse<Token>> {
        return userRemoteDataSource.login(loginParam)
    }

    override suspend fun join(registerParam: RegisterParam): Result<CommonResponse<Unit>> {
        return userRemoteDataSource.join(registerParam)
    }

    override suspend fun requestEmailAuthCode(email: String): Result<CommonResponse<Unit>>{
        return userRemoteDataSource.requestEmailAuthCode(email)
    }

    override suspend fun verifyEmailAuthCode(email: String, emailAuthCode: String): Result<CommonResponse<Boolean>>{
        return userRemoteDataSource.verifyEmailAuthCode(email, emailAuthCode)
    }

}