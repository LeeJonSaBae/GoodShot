package com.ijonsabae.data.usecase.profile

import com.ijonsabae.data.model.PresignedURLParam
import com.ijonsabae.data.retrofit.ProfileService
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.PresignedURL
import com.ijonsabae.domain.usecase.profile.GetPresignedURLUseCase
import javax.inject.Inject

private const val TAG = "굿샷_GetPresignedURLUseCaseImpl"

class GetProfileImgUseCaseImpl @Inject constructor(
    private val profileService: ProfileService
) : GetPresignedURLUseCase {

    override suspend fun invoke(imageExtension: String): Result<CommonResponse<PresignedURL>> {

        val requestBody = PresignedURLParam(imageExtension = imageExtension)
        return profileService.getProfilePresignedURL(
            requestBody = requestBody
        )

    }
}