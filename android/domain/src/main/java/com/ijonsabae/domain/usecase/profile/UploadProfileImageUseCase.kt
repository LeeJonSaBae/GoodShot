package com.ijonsabae.domain.usecase.profile

import java.net.URI

interface UploadProfileImageUseCase {

    suspend operator fun invoke(
        presignedUrl: String,
        imageURI: URI
    ): Result<Int>


}