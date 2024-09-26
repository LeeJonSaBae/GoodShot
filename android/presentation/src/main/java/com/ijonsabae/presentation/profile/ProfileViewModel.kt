package com.ijonsabae.presentation.profile

import androidx.lifecycle.ViewModel
import com.ijonsabae.domain.usecase.profile.GetProfileImgUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileImgUseCase: GetProfileImgUseCase
) : ViewModel() {

    suspend fun getPresignedURL(accessToken: String, imageExtension: String) {
        val result = profileImgUseCase(accessToken, imageExtension).getOrThrow()
        val presignedURL = result.presignedUrl
    }
}