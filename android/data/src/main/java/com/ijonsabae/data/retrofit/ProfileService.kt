package com.ijonsabae.data.retrofit

import com.ijonsabae.data.model.CommonResponse
import com.ijonsabae.domain.model.Profile
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ProfileService {
    @POST("presigned")
    suspend fun getProfilePresignedURL(
        @Header("Authorization") accessToken: String,
        @Body requestBody: RequestBody
    ): CommonResponse<Profile>
}