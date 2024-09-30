package com.ijonsabae.data.datastore.remote

import com.ijonsabae.data.retrofit.TokenService
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Token
import javax.inject.Inject

class TokenRemoteDataSource @Inject constructor(private val tokenService: TokenService) {
    suspend fun getToken(): Result<CommonResponse<Token>> {
        return tokenService.reissue()
    }
}