package com.ijonsabae.domain.usecase.profile

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Profile

interface GetProfileInfoUseCase {
    suspend operator fun invoke(
    ): Result<CommonResponse<Profile>>
}