package com.ijonsabae.presentation.home
import org.jsoup.Jsoup
import java.io.IOException

object YouTubeUtils {
    fun getVideoTitle(videoId: String): String {
        val url = "https://www.youtube.com/watch?v=$videoId"
        return try {
            val doc = Jsoup.connect(url).get()
            doc.select("meta[name=title]").first()?.attr("content") ?: "제목을 찾을 수 없습니다."
        } catch (e: IOException) {
            "오류 발생: ${e.message}"
        }
    }
}