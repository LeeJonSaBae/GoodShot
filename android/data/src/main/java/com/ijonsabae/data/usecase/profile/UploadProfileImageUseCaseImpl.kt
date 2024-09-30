package com.ijonsabae.data.usecase.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ijonsabae.data.retrofit.ProfileService
import com.ijonsabae.domain.usecase.profile.UploadProfileImageUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File
import java.net.URI
import javax.inject.Inject

private const val TAG = "굿샷_UploadProfileImageUseCa"

class UploadProfileImageUseCaseImpl @Inject constructor(
    private val profileService: ProfileService,
    @ApplicationContext private val context: Context
) : UploadProfileImageUseCase {

    override suspend fun invoke(
        presignedUrl: String,
        imageURI: URI
    ): Result<Int> {
        val requestBody = createRequestBodyFromUri(imageURI)
        val result = profileService.uploadProfileImage(presignedUrl, requestBody)
        Log.d(TAG, "UploadProfileImageUseCaseImpl 까지 왔음 -> isSuccess? = ${result.isSuccess}")

        return result
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