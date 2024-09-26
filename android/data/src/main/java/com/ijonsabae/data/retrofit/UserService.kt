package com.ijonsabae.data.retrofit

import com.ijonsabae.data.model.RegisterParam
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.LoginParam
import com.ijonsabae.domain.model.Token
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("users/login")
    suspend fun login(@Body loginParam: LoginParam): CommonResponse<Token>
    @POST("users/join")
    suspend fun join(@Body registerParam: RegisterParam): CommonResponse<Unit>
    @POST("users/reissue")
    suspend fun reissue(@Body refreshToken: String): CommonResponse<Unit>
    @POST("users/exit")
    suspend fun exit(): CommonResponse<Unit>
}