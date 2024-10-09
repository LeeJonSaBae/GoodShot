package com.ijonsabae.data.datasource.local

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ijonsabae.domain.model.SwingFeedback
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val TAG = "ReplayLocalAllSwingFeedbackPagingSource_싸피"
class ReplayLocalAllSwingFeedbackPagingSource @Inject constructor(
    private val swingFeedbackLocalDataSource: SwingFeedbackLocalDataSource,
    private val tokenLocalDataSource: TokenLocalDataSource
) : PagingSource<Int, SwingFeedback>() {
    override fun getRefreshKey(state: PagingState<Int, SwingFeedback>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SwingFeedback> {
        val userID = runBlocking { tokenLocalDataSource.getUserId() }
        return try {
            val currentPage = params.key ?: 0
            val response = swingFeedbackLocalDataSource.getAllSwingFeedback(1, params.loadSize, currentPage)
            LoadResult.Page(
                data = response,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if(response.isEmpty()) null else currentPage + 1
            )
        } catch (exception: Exception) {
            Log.d(TAG, "load: 에러")
            Log.d(TAG, "load: ${exception.message}")
            LoadResult.Error(exception)
        }
    }
}