package com.ijonsabae.data.usecase.consult

import androidx.paging.PagingData
import com.ijonsabae.data.repository.ConsultRepository
import com.ijonsabae.domain.model.Expert
import com.ijonsabae.domain.usecase.consult.GetConsultantListUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConsultantListUseCaseImpl @Inject constructor(private val consultRepository: ConsultRepository) :
    GetConsultantListUseCase {
    override fun invoke(): Flow<PagingData<Expert>> {
        return consultRepository.getConsultantList()
    }
}