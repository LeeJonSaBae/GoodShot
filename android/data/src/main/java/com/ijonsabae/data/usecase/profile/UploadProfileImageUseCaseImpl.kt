package com.ijonsabae.data.usecase.profile

import android.content.Context
import android.net.Uri
import com.ijonsabae.data.retrofit.ProfileService
import com.ijonsabae.data.retrofit.UploadImageService
import com.ijonsabae.domain.model.UploadProfileResponse
import com.ijonsabae.domain.usecase.profile.UploadProfileImageUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File
import java.net.URI
import javax.inject.Inject

private const val TAG = "굿샷_UploadProfileImageUseCa"

class UploadProfileImageUseCaseImpl @Inject constructor(
    private val uploadImageService: UploadImageService,
    @ApplicationContext private val context: Context
) : UploadProfileImageUseCase {

    override suspend fun invoke(
        presignedUrl: String,
        imageURI: URI
    ): Result<UploadProfileResponse> {
        val requestBody = createRequestBodyFromUri(imageURI)

        return uploadImageService.uploadProfileImage(presignedUrl, requestBody)
    }

    private fun createRequestBodyFromUri(uri: URI): RequestBody {
        val inputStream = context.contentResolver.openInputStream(Uri.parse(uri.toString()))
            ?: throw IllegalStateException("InputStream 얻기 실패")

        val file = File(context.cacheDir, "profile")

        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return RequestBody.create(
            "image/*".toMediaTypeOrNull(),
            file
        )
    }
}