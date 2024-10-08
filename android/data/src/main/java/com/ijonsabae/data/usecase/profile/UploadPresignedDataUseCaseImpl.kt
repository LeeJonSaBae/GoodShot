package com.ijonsabae.data.usecase.profile

import android.content.Context
import android.net.Uri
import com.ijonsabae.data.retrofit.PresignedService
import com.ijonsabae.domain.usecase.profile.UploadPresignedDataUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.net.URI
import javax.inject.Inject

private const val TAG = "굿샷_UploadProfileImageUseCa"

class UploadPresignedDataUseCaseImpl @Inject constructor(
    private val uploadImageService: PresignedService,
    @ApplicationContext private val context: Context
) : UploadPresignedDataUseCase {

    override suspend fun invoke(
        presignedUrl: String,
        imageURI: URI
    ): Result<Unit> {
        val requestBody = createRequestBodyFromUri(imageURI)
        return uploadImageService.uploadImage(presignedUrl, requestBody)
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

        return file
            .asRequestBody("image/*".toMediaTypeOrNull())
    }
}