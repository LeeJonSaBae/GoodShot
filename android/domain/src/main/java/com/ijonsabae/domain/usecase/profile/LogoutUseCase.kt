package com.ijonsabae.domain.usecase.profile

import com.ijonsabae.domain.model.CommonResponse

interface LogoutUseCase {
    suspend operator fun invoke(
    ): Result<CommonResponse<String?>>
}