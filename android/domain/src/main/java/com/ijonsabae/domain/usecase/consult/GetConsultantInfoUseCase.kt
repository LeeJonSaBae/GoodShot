package com.ijonsabae.domain.usecase.consult

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.ExpertDetail

interface GetConsultantInfoUseCase {
    suspend operator fun invoke(id: Int): Result<CommonResponse<ExpertDetail>>
}