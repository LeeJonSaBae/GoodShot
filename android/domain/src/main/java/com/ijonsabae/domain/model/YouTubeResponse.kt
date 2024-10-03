package com.ijonsabae.domain.model

data class YouTubeResponse(
    val items: List<VideoItem>
) {
    data class VideoItem(
        val id: Id,
        val snippet: Snippet
    ) {
        data class Id(
        val videoId: String
        )

        data class Snippet(
        val title: String,
        val thumbnails: Thumbnails
        ) {
            data class Thumbnails(
                val high: Thumbnail
            ) {
                data class Thumbnail(
                    val url: String
                )
            }
        }
    }
    companion object{
        val EMPTY = YouTubeResponse(
            listOf()
        )
    }
}