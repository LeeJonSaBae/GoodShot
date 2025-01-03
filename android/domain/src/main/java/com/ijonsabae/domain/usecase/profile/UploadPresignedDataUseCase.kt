package com.ijonsabae.domain.usecase.profile

import java.net.URI

interface UploadPresignedDataUseCase {

    suspend operator fun invoke(
        presignedUrl: String,
        imageURI: URI
    ): Result<Unit>


}