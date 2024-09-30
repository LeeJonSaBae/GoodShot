package com.ijonsabae.presentation.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.ijonsabae.domain.usecase.profile.GetPresignedURLUseCase
import com.ijonsabae.domain.usecase.profile.UploadProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URI
import javax.inject.Inject

private const val TAG = "굿샷_ProfileViewModel"

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getPresignedURLUseCase: GetPresignedURLUseCase,
    private val uploadProfileImageUseCase: UploadProfileImageUseCase
) : ViewModel() {

    private val _presignedUrl = MutableStateFlow<String?>(null)
    val presignedUrl: StateFlow<String?> = _presignedUrl.asStateFlow()

    suspend fun getPresignedURL(accessToken: String, imageExtension: String) {

        val result = getPresignedURLUseCase(accessToken, imageExtension).getOrThrow()
        _presignedUrl.value = result.data.presignedUrl
    }

    suspend fun uploadProfileImage(presignedUrl: String, image: Uri) {
        val result = uploadProfileImageUseCase(presignedUrl, URI(image.toString())).getOrThrow()
    }


}