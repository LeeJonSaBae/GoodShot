package com.ijonsabae.domain.usecase.profile

import com.ijonsabae.domain.model.CommonResponse

interface ChangePasswordUseCase {
    suspend operator fun invoke(oldPassword: String, newPassword: String) : Result<CommonResponse<Unit>>
}