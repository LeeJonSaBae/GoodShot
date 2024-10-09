package com.ijonsabae.data.repository

import com.ijonsabae.data.datasource.local.TokenLocalDataSource
import com.ijonsabae.data.datasource.local.UserLocalDataSource
import com.ijonsabae.data.datasource.remote.TokenRemoteDataSource
import com.ijonsabae.data.datasource.remote.UserRemoteDataSource
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Token
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val tokenRemoteDataSource: TokenRemoteDataSource,
    private val tokenLocalDataSource: TokenLocalDataSource,
    private val userLocalDataSource: UserLocalDataSource,
) : TokenRepository {
    override suspend fun getLocalAccessToken(): String?{
        return tokenLocalDataSource.getAccessToken()
    }

    override suspend fun getLocalRefreshToken(): String?{
        return tokenLocalDataSource.getRefreshToken()
    }

    override suspend fun getUserId(): Long {
        return tokenLocalDataSource.getUserId()
    }

    override suspend fun setLocalToken(token: Token){
        tokenLocalDataSource.setToken(token)
        tokenLocalDataSource.setLocalTokenCreatedTime()
    }

    override suspend fun clearToken() {
        userLocalDataSource.clearUserName()
        return tokenLocalDataSource.clear()
    }

    override suspend fun reissueRemoteToken():Result<CommonResponse<Token>>{
        return tokenRemoteDataSource.getToken()
    }

    override suspend fun getLocalTokenCreatedTime(): Long? {
        return tokenLocalDataSource.getLocalTokenCreatedTime()
    }

    override suspend fun getLocalAccessTokenFlow(): StateFlow<String?> {
        return tokenLocalDataSource.getLocalAccessTokenFlow()
    }
}