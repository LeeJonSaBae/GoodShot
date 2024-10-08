package com.ijonsabae.presentation.home
import org.jsoup.Jsoup

object YouTubeUtils {
    fun getYoutubeDto(videoId: String): YoutubeDTO {
        val url = "https://www.youtube.com/watch?v=$videoId"
        val doc = Jsoup.connect(url).get()
        val title = doc.select("meta[name=title]").first()?.attr("content") ?: "제목을 찾을 수 없습니다."

        return YoutubeDTO(
            title = title,
            isVisible = false,
            link = "https://www.youtube.com/watch?v=$videoId",
            thumbnail = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg",
            alternativeThumbnail = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg",
        )

//        } catch (e: IOException) {
//            "오류 발생: ${e.message}"
//        }
    }
}