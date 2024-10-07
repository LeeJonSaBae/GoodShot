package com.ijonsabae.presentation.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Profile
import com.ijonsabae.domain.usecase.login.GetLocalAccessTokenUseCase
import com.ijonsabae.domain.usecase.profile.GetPresignedURLUseCase
import com.ijonsabae.domain.usecase.profile.GetProfileInfoUseCase
import com.ijonsabae.domain.usecase.profile.LogoutUseCase
import com.ijonsabae.domain.usecase.profile.UpdateProfileUseCase
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
    private val uploadProfileImageUseCase: UploadProfileImageUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val getLocalAccessTokenUseCase: GetLocalAccessTokenUseCase
) : ViewModel() {

    private val _isLogin: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLogin: StateFlow<Boolean> = _isLogin

    private val _profileInfo = MutableStateFlow<Profile?>(null)
    val profileInfo: StateFlow<Profile?> = _profileInfo.asStateFlow()

    private val _presignedUrl = MutableStateFlow<String?>(null)
    val presignedUrl: StateFlow<String?> = _presignedUrl.asStateFlow()

    private val _imageUrl = MutableStateFlow<String?>(null)
    val imageUrl: StateFlow<String?> = _imageUrl.asStateFlow()

    private val _cropUri = MutableStateFlow<Uri>(Uri.EMPTY)
    val croppedUri: StateFlow<Uri> = _cropUri.asStateFlow()

    private val _isLogoutSucceed = MutableStateFlow<Int?>(null)
    val isLogoutSucceed: StateFlow<Int?> = _isLogoutSucceed.asStateFlow()

//    private val _updateProfileResult = MutableStateFlow<Result<CommonResponse<Unit>>>(null)
//    val isUpdateProfileSucceed: StateFlow<Int?> = _updateProfileResult.asStateFlow()

    suspend fun setLoginStatus(login:Boolean){
        _isLogin.emit(login)
    }

    suspend fun getProfileInfo() {
        val result = getProfileInfoUseCase().getOrThrow()
        _profileInfo.value = Profile(profileUrl = result.data.profileUrl, name = result.data.name)
    }

    suspend fun getPresignedURL(imageExtension: String) {

        val result = getPresignedURLUseCase(imageExtension).getOrThrow()
        _presignedUrl.value = result.data.presignedUrl
        _imageUrl.value = result.data.imageUrl
    }

    suspend fun setCroppedUri(uri: Uri){
        _cropUri.emit(uri)
    }

    suspend fun uploadProfileImage(presignedUrl: String, image: Uri): Result<Unit> {
        return uploadProfileImageUseCase(presignedUrl, URI(image.toString()))
    }

    suspend fun logout() {
        val result = logoutUseCase().getOrThrow()
        _isLogoutSucceed.value = result.code
        _isLogin.emit(false)
    }

    suspend fun updateProfile(image: String?): Result<CommonResponse<Unit>>{
        return updateProfileUseCase(image.toString())
    }

    suspend fun getToken(): String? {
        return getLocalAccessTokenUseCase()
    }

}