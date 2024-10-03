package com.ijonsabae.domain.usecase.consult

import androidx.paging.PagingData
import com.ijonsabae.domain.model.Expert
import kotlinx.coroutines.flow.Flow

interface GetConsultantListUseCase {
    operator fun invoke(): Flow<PagingData<Expert>>
}