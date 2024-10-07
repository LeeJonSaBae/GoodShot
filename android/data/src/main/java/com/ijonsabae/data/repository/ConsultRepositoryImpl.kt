package com.ijonsabae.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ijonsabae.data.datasource.remote.ExpertRemoteDataSource
import com.ijonsabae.data.datasource.remote.ExpertRemotePagingSource
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Expert
import com.ijonsabae.domain.model.ExpertDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConsultRepositoryImpl @Inject constructor(
    private val expertRemotePagingSource: ExpertRemotePagingSource,
    private val expertRemoteDataSource: ExpertRemoteDataSource
) :
    ConsultRepository {
    override fun getConsultantList(): Flow<PagingData<Expert>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { expertRemotePagingSource }
        ).flow
    }

    override suspend fun getConsultantInfo(id: Int): Result<CommonResponse<ExpertDetail>> {
        return expertRemoteDataSource.getConsultantInfo(id)
    }
}