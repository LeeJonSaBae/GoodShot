package com.ijonsabae.data.repository

import androidx.paging.PagingData
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Expert
import com.ijonsabae.domain.model.ExpertDetail
import kotlinx.coroutines.flow.Flow

interface ConsultRepository {
    fun getConsultantList(): Flow<PagingData<Expert>>
    suspend fun getConsultantInfo(id: Int): Result<CommonResponse<ExpertDetail>>
}