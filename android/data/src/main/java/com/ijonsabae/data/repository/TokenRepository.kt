package com.ijonsabae.data.repository

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Token
import kotlinx.coroutines.flow.StateFlow

interface TokenRepository {
    suspend fun getLocalAccessToken(): String?
    suspend fun getLocalRefreshToken(): String?
    suspend fun getUserId(): Long
    suspend fun setLocalToken(token: Token)
    suspend fun clearToken()
    suspend fun reissueRemoteToken():Result<CommonResponse<Token>>
    suspend fun getLocalTokenCreatedTime(): Long?
    suspend fun getLocalAccessTokenFlow(): StateFlow<String?>
}