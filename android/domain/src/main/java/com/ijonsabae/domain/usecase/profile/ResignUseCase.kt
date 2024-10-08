package com.ijonsabae.domain.usecase.profile

import com.ijonsabae.domain.model.CommonResponse

interface ResignUseCase {
    suspend operator fun invoke(): Result<CommonResponse<Unit>>
}