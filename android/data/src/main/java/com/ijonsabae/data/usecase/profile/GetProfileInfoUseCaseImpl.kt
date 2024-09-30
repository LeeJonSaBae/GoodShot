package com.ijonsabae.data.usecase.profile

import com.ijonsabae.data.retrofit.ProfileService
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Profile
import com.ijonsabae.domain.usecase.profile.GetProfileInfoUseCase
import javax.inject.Inject

class GetProfileInfoUseCaseImpl @Inject constructor(
    private val profileService: ProfileService
) : GetProfileInfoUseCase {

    override suspend fun invoke(): Result<CommonResponse<Profile>> {
        return profileService.getProfileInfo()
    }
}