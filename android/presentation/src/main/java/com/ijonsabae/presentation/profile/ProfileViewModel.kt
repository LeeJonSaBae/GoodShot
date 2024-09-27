package com.ijonsabae.presentation.profile

import androidx.lifecycle.ViewModel
import com.ijonsabae.domain.usecase.profile.GetPresignedURLUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getPresignedURLUseCase: GetPresignedURLUseCase
) : ViewModel() {

    suspend fun getPresignedURL(accessToken: String, imageExtension: String) {
        val result = getPresignedURLUseCase(accessToken, imageExtension).getOrThrow()

        val presignedURL = result.data.presignedUrl
        val imageURL = result.data.imageUrl
    }
}