package com.ijonsabae.domain.usecase.home

import com.ijonsabae.domain.model.YouTubeResponse

interface GetSearchVideosUseCase {
    suspend operator fun invoke(part: String, query: String, maxResults: Int): YouTubeResponse
}