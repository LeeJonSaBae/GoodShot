package com.ijonsabae.data.repository

import com.ijonsabae.domain.model.YouTubeResponse

interface YoutubeRepository {
    suspend fun getSearchVideos(part: String, query: String, maxResults: Int): Result<YouTubeResponse>
}