package com.ijonsabae.data.repository

import androidx.paging.PagingData
import com.ijonsabae.domain.model.Expert
import kotlinx.coroutines.flow.Flow

interface ConsultRepository {
    fun getConsultantList(): Flow<PagingData<Expert>>
}