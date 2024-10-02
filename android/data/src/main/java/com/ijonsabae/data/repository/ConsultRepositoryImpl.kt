package com.ijonsabae.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ijonsabae.data.datastore.remote.ExpertRemotePagingSource
import com.ijonsabae.domain.model.Expert
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConsultRepositoryImpl @Inject constructor(private val expertRemotePagingSource: ExpertRemotePagingSource):
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


}