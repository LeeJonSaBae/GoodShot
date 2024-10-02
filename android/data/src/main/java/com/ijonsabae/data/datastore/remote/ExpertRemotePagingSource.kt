package com.ijonsabae.data.datastore.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ijonsabae.domain.model.Expert
import javax.inject.Inject

class ExpertRemotePagingSource @Inject constructor(
    private val expertRemoteDataSource: ExpertRemoteDataSource,
): PagingSource<Int, Expert>() {
    override fun getRefreshKey(state: PagingState<Int, Expert>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Expert> {
        return try {
            val currentPage = params.key ?: 0
            val response = expertRemoteDataSource.getConsultantList(currentPage, params.loadSize)

            val data = response.getOrThrow().data.expertsList
            val nextPageExist = response.getOrThrow().data.hasNext

            LoadResult.Page(
                data = data,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (nextPageExist) currentPage + 1 else null
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

}