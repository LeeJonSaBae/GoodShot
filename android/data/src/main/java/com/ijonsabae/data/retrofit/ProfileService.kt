package com.ijonsabae.data.retrofit

import com.ijonsabae.data.model.PresignedURLParam
import com.ijonsabae.data.model.UpdateProfileParam
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.PresignedURL
import com.ijonsabae.domain.model.Profile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ProfileService {
    @GET("users/profile")
    suspend fun getProfileInfo(): Result<CommonResponse<Profile>>

    @POST("presigned")
    suspend fun getProfilePresignedURL(
        @Body requestBody: PresignedURLParam
    ): Result<CommonResponse<PresignedURL>>

    @PUT("users/profile")
    suspend fun updateProfile(
        @Body requestBody: UpdateProfileParam
    ): Result<CommonResponse<Unit>>
}
