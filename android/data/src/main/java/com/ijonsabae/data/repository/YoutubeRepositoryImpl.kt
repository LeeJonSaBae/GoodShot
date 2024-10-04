package com.ijonsabae.data.repository

import com.ijonsabae.data.datasource.remote.YoutubeRemoteDataSource
import com.ijonsabae.domain.model.YouTubeResponse
import javax.inject.Inject

class YoutubeRepositoryImpl @Inject constructor(private val youtubeRemoteDataSource: YoutubeRemoteDataSource): YoutubeRepository {
    override suspend fun getSearchVideos(part: String, query: String, maxResults: Int): Result<YouTubeResponse>{
        return youtubeRemoteDataSource.getSearchVideos(
            part = part,
            query = query,
            maxResults = maxResults
        )
    }
}