package com.ijonsabae.data.retrofit

import com.ijonsabae.data.model.PresignedURLParam
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.PresignedURL
import com.ijonsabae.domain.model.Profile
import com.ijonsabae.domain.model.UploadProfileResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url

interface UploadImageService {
    @PUT
    suspend fun uploadProfileImage(
        @Url presignedURL: String,
        @Body image: RequestBody
    ): Result<UploadProfileResponse>
}