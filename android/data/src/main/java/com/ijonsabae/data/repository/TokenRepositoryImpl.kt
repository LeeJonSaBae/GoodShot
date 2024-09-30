package com.ijonsabae.data.repository

import com.ijonsabae.data.datastore.local.TokenLocalDataSource
import com.ijonsabae.data.datastore.remote.TokenRemoteDataSource
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Token
import com.ijonsabae.domain.repository.TokenRepository
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val tokenRemoteDataSource: TokenRemoteDataSource,
    private val tokenLocalDataSource: TokenLocalDataSource
) : TokenRepository {
    override suspend fun getLocalAccessToken(): String?{
        return tokenLocalDataSource.getAccessToken()
    }

    override suspend fun getLocalRefreshToken(): String?{
        return tokenLocalDataSource.getRefreshToken()
    }

    override suspend fun setLocalToken(token: Token){
        tokenLocalDataSource.setToken(token)
        tokenLocalDataSource.setLocalTokenCreatedTime()
    }

    override suspend fun clearToken() {
        return tokenLocalDataSource.clear()
    }

    override suspend fun reissueRemoteToken():Result<CommonResponse<Token>>{
        return tokenRemoteDataSource.getToken()
    }

    override suspend fun getLocalTokenCreatedTime(): Long? {
        return tokenLocalDataSource.getLocalTokenCreatedTime()
    }
}