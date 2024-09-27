package com.ijonsabae.domain.usecase.profile

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Profile

interface GetPresignedURLUseCase {

    suspend operator fun invoke(
        accessToken: String,
        imageExtension: String
    ): Result<CommonResponse<Profile>>
}