package com.ijonsabae.presentation.profile

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.ijonsabae.domain.model.Profile
import com.ijonsabae.domain.usecase.profile.GetPresignedURLUseCase
import com.ijonsabae.domain.usecase.profile.GetProfileInfoUseCase
import com.ijonsabae.domain.usecase.profile.UploadProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URI
import javax.inject.Inject

private const val TAG = "굿샷_ProfileViewModel"

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getProfileInfoUseCase: GetProfileInfoUseCase,
    private val getPresignedURLUseCase: GetPresignedURLUseCase,
    private val uploadProfileImageUseCase: UploadProfileImageUseCase
) : ViewModel() {

    private val _profileInfo = MutableStateFlow<Profile?>(null)
    val profileInfo: StateFlow<Profile?> = _profileInfo.asStateFlow()

    private val _presignedUrl = MutableStateFlow<String?>(null)
    val presignedUrl: StateFlow<String?> = _presignedUrl.asStateFlow()

    suspend fun getProfileInfo() {
        val result = getProfileInfoUseCase().getOrThrow()
        _profileInfo.value = Profile(profileUrl = result.data.profileUrl, name = result.data.name)
    }

    suspend fun getPresignedURL(accessToken: String, imageExtension: String) {

        val result = getPresignedURLUseCase(accessToken, imageExtension).getOrThrow()
        _presignedUrl.value = result.data.presignedUrl
    }

    suspend fun uploadProfileImage(presignedUrl: String, image: Uri) {
        val result = uploadProfileImageUseCase(presignedUrl, URI(image.toString())).getOrThrow()
        Toast.makeText(
            context,
            "uploadProfileImage() : etag = ${result.etag}, requestId = ${result.requestId}",
            Toast.LENGTH_SHORT
        ).show()
    }


}