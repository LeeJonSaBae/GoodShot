package com.ijonsabae.data.usecase.profile

import com.ijonsabae.data.retrofit.SwingService
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.TotalReport
import com.ijonsabae.domain.usecase.profile.GetTotalReportUseCase
import javax.inject.Inject

class GetTotalReportUseCaseImpl @Inject constructor(
    private val swingService: SwingService
) : GetTotalReportUseCase {

    override suspend fun invoke(): Result<CommonResponse<TotalReport>> {
        return swingService.getTotalReport()
    }
}