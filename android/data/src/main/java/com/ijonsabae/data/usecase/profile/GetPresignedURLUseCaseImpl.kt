package com.ijonsabae.data.usecase.profile

import com.ijonsabae.data.model.PresignedURLParam
import com.ijonsabae.data.retrofit.ProfileService
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.PresignedURL
import com.ijonsabae.domain.usecase.profile.GetPresignedURLUseCase
import javax.inject.Inject


class GetProfileImgUseCaseImpl @Inject constructor(
    private val profileService: ProfileService
) : GetPresignedURLUseCase {

    override suspend operator fun invoke(
        imageExtension: String
    ): Result<CommonResponse<PresignedURL>> {

        val requestBody = PresignedURLParam(imageExtension = imageExtension)
        return profileService.getProfilePresignedURL(
            requestBody = requestBody
        )

    }
}