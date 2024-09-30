package com.ijonsabae.domain.usecase.login

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Token

interface ReissueTokenUseCase {
    suspend operator fun invoke() : Result<CommonResponse<Token>>
}