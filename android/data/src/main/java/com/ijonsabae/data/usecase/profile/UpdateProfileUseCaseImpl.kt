package com.ijonsabae.data.usecase.profile

import com.ijonsabae.data.retrofit.ProfileService
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.usecase.profile.UpdateProfileUseCase
import javax.inject.Inject

class UpdateProfileUseCaseImpl @Inject constructor(
    private val profileService: ProfileService
) : UpdateProfileUseCase {

    override suspend fun invoke(imageUrl: String): Result<CommonResponse<Unit>> {
        return profileService.updateProfile()
    }


}