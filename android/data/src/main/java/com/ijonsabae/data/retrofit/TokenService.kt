package com.ijonsabae.data.retrofit

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Token
import retrofit2.http.Header
import retrofit2.http.POST

interface TokenService {
    @POST("users/reissue")
    suspend fun reissue(): Result<CommonResponse<Token>>
}