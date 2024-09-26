package com.ijonsabae.domain.usecase.profile

import com.ijonsabae.domain.model.Profile

interface GetProfileImgUseCase {

    suspend operator fun invoke(
        accessToken: String,
        imageExtension: String
    ): Result<Profile>
}