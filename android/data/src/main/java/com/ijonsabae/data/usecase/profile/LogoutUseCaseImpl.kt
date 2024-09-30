package com.ijonsabae.data.usecase.profile

import com.ijonsabae.data.retrofit.ProfileService
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.usecase.profile.LogoutUseCase
import javax.inject.Inject

class LogoutUseCaseImpl @Inject constructor(
    private val profileService: ProfileService
) : LogoutUseCase {

    override suspend fun invoke(): Result<CommonResponse<String?>> {
        return profileService.logout()
    }
}