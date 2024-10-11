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

////TODO 영민 : videoId로 video정보 가져오기 -> title에 들어가는 getVideoTitle main에서 실행안되게 고려 필요
//fun convertVideoIdToYoutubeDTO(videoId: String): YoutubeDTO{
//    return YoutubeDTO(
//        title = YouTubeUtils.getYoutubeDto(videoId),
//        link = "https://www.youtube.com/watch?v=$videoId",
//        isVisible = false,
//        thumbnail = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg",
//        alternativeThumbnail = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
//    )
//}