package com.ijonsabae.data.retrofit

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Url

interface UploadImageService {
    @PUT
    suspend fun uploadProfileImage(
        @Url presignedURL: String,
        @Body image: RequestBody
    ): Result<Unit>
}