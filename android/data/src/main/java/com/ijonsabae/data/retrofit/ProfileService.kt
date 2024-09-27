package com.ijonsabae.data.retrofit

import com.ijonsabae.data.model.ProfileParam
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Profile
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ProfileService {
    @POST("presigned")
    suspend fun getProfilePresignedURL(
        @Header("Authorization") accessToken: String,
        @Body requestBody: ProfileParam
    ): Result<CommonResponse<Profile>>
}