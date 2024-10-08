package com.ijonsabae.domain.usecase.profile

import com.ijonsabae.domain.model.CommonResponse

interface UpdateProfileUseCase {

    suspend operator fun invoke(
        imageUrl: String
    ): Result<CommonResponse<Unit>>

}