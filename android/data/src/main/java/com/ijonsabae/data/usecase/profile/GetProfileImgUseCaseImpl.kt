package com.ijonsabae.data.usecase.profile

import com.ijonsabae.data.model.ProfileParam
import com.ijonsabae.data.retrofit.ProfileService
import com.ijonsabae.domain.model.Profile
import com.ijonsabae.domain.usecase.profile.GetProfileImgUseCase
import javax.inject.Inject

class GetProfileImgUseCaseImpl @Inject constructor(
    private val profileService: ProfileService
) : GetProfileImgUseCase {

    override suspend fun invoke(accessToken: String, imageExtension: String): Result<Profile> =
        kotlin.runCatching {
            val requestBody = ProfileParam(imageExtension = imageExtension)
            profileService.getProfilePresignedURL(
                accessToken = accessToken,
                requestBody = requestBody
            ).data
        }
}