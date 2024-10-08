package com.ijonsabae.data.usecase.consult

import com.ijonsabae.data.repository.ConsultRepository
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.ExpertDetail
import com.ijonsabae.domain.usecase.consult.GetConsultantInfoUseCase
import javax.inject.Inject

class GetConsultantInfoUseCaseImpl @Inject constructor(private val consultRepository: ConsultRepository) :
    GetConsultantInfoUseCase {
    override suspend fun invoke(id: Int): Result<CommonResponse<ExpertDetail>> {
        return consultRepository.getConsultantInfo(id)
    }

}