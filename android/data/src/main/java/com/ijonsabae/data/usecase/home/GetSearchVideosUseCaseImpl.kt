package com.ijonsabae.data.usecase.home

import com.ijonsabae.data.repository.YoutubeRepository
import com.ijonsabae.domain.model.YouTubeResponse
import com.ijonsabae.domain.usecase.home.GetSearchVideosUseCase
import javax.inject.Inject

class GetSearchVideosUseCaseImpl @Inject constructor(private val youtubeRepository: YoutubeRepository): GetSearchVideosUseCase {
    override suspend operator fun invoke(part: String, query: String, maxResults: Int): YouTubeResponse{
        return youtubeRepository.getSearchVideos(
            part = part,
            query = query,
            maxResults = maxResults
        )
    }
}