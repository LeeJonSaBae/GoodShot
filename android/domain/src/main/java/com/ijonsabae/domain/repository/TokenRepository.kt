package com.ijonsabae.domain.repository

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Token

interface TokenRepository {
    suspend fun getLocalAccessToken(): String?
    suspend fun getLocalRefreshToken(): String?
    suspend fun setLocalToken(token: Token)
    suspend fun clearToken()
    suspend fun reissueRemoteToken():Result<CommonResponse<Token>>
    suspend fun getLocalTokenCreatedTime(): Long?
}