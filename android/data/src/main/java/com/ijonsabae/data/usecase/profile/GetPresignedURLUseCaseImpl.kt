package com.ijonsabae.data.usecase.profile

import com.ijonsabae.data.model.ProfileParam
import com.ijonsabae.data.retrofit.ProfileService
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Profile
import com.ijonsabae.domain.usecase.profile.GetProfileImgUseCase
import javax.inject.Inject


class GetProfileImgUseCaseImpl @Inject constructor(
    private val profileService: ProfileService
) : GetPresignedURLUseCase {

    override suspend operator fun invoke(
        accessToken: String,
        imageExtension: String
    ): Result<CommonResponse<Profile>> {
        val requestBody = ProfileParam(imageExtension = imageExtension)
        return profileService.getProfilePresignedURL(
            accessToken = accessToken,
            requestBody = requestBody
        )
    }
}