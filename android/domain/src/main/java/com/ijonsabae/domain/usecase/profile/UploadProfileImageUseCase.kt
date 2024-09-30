package com.ijonsabae.domain.usecase.profile

import com.ijonsabae.domain.model.UploadProfileResponse
import java.net.URI


interface UploadProfileImageUseCase {

    suspend operator fun invoke(
        presignedUrl: String,
        imageURI: URI
    ): Result<UploadProfileResponse>


}