package com.ijonsabae.domain.usecase.profile

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.TotalReport

interface GetTotalReportUseCase {

    suspend operator fun invoke(): Result<CommonResponse<TotalReport>>
}