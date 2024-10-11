package com.ijonsabae.data.retrofit

import com.ijonsabae.data.model.GenerateTemporaryPassWordParam
import com.ijonsabae.data.model.RequestEmailAuthCodeParam
import com.ijonsabae.domain.model.ChangePasswordParam
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.LoginParam
import com.ijonsabae.domain.model.RegisterParam
import com.ijonsabae.domain.model.Token
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface UserService {
    @POST("users/login")
    suspend fun login(@Body loginParam: LoginParam): Result<CommonResponse<Token>>
    @POST("users/join")
    suspend fun join(@Body registerParam: RegisterParam): Result<CommonResponse<Unit>>
    @POST("users/exit")
    suspend fun exit(): Result<CommonResponse<Unit>>
    @POST("users/email")
    suspend fun sendEmailAuthCode(@Body email: RequestEmailAuthCodeParam): Result<CommonResponse<Unit>>
    @GET("users/email")
    suspend fun checkEmailAuthCode(@Query("email") email: String, @Query("code") code: String): Result<CommonResponse<Boolean>>
    @GET("users/check-email")
    suspend fun checkEmailDuplicated(@Query("email") email: String): Result<CommonResponse<Boolean>>
    @POST("users/temporary-password")
    suspend fun generateTemporaryPassWord(@Body generatedTemporaryPassWordParam: GenerateTemporaryPassWordParam): Result<CommonResponse<Unit>>
    @POST("users/logout")
    suspend fun logout(): Result<CommonResponse<Unit>>
    @PUT("users/password")
    suspend fun changePassword(@Body changePasswordParam: ChangePasswordParam):Result<CommonResponse<Unit>>
    @GET("users/name")
    suspend fun getUserName():Result<CommonResponse<String>>
}