package com.ijonsabae.data.datasource.remote

import com.ijonsabae.data.BuildConfig
import com.ijonsabae.data.retrofit.YoutubeService
import com.ijonsabae.domain.model.YouTubeResponse
import javax.inject.Inject

const val YOUTUBE_KEY = BuildConfig.YOUTUBE_KEY
class YoutubeRemoteDataSource @Inject constructor(
    private val youtubeService: YoutubeService
) {
    suspend fun getSearchVideos(
                               part: String,
                               query: String,
                               maxResults: Int): Result<YouTubeResponse>{
        val result = youtubeService.searchVideos(
            part = part,
            query = query,
            apiKey = YOUTUBE_KEY,
            maxResults = maxResults
        )
        return result
    }
}