package com.ijonsabae.presentation.home

import com.ijonsabae.domain.model.YouTubeResponse

data class YoutubeDTO(
    val title: String,
    val thumbnail: String,
    val alternativeThumbnail: String,
    val link: String?,
    var isVisible: Boolean = false
)

fun convertVideoItemToYoutubeDTO(videoItem: YouTubeResponse.VideoItem): YoutubeDTO{
    return YoutubeDTO(
        title = videoItem.snippet.title,
        link = "https://www.youtube.com/watch?v="+videoItem.id.videoId,
        isVisible = false,
        thumbnail = "https://img.youtube.com/vi/" + videoItem.id.videoId + "/maxresdefault.jpg",
        alternativeThumbnail = videoItem.snippet.thumbnails.high.url
    )
}