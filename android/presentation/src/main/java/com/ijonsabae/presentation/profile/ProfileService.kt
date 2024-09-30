package com.ijonsabae.presentation.profile

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.PresignedURL
import com.ijonsabae.domain.model.Profile
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url

interface ProfileService {
    @GET("users/profile")
    suspend fun getProfileInfo(): Result<CommonResponse<Profile>>

    @POST("presigned")
    suspend fun getProfilePresignedURL(
        @Body requestBody: PresignedURLParam
    ): Result<CommonResponse<PresignedURL>>

    @PUT
    suspend fun uploadProfileImage(
        @Url presignedURL: String,
        @Body image: RequestBody
    ): Result<CommonResponse<Int>>
}