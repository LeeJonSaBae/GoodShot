package com.ijonsabae.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ijonsabae.domain.model.Profile
import com.ijonsabae.domain.usecase.home.GetSearchVideosUseCase
import com.ijonsabae.domain.usecase.profile.GetProfileInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getSearchVideosUseCase: GetSearchVideosUseCase,
    private val getProfileInfoUseCase: GetProfileInfoUseCase
) : ViewModel() {
    private val _youtubeIdList =
        listOf("Rogr71lXTg0", "-gNimeR9U5g", "jbxnVBA0kB0", "N5FpZ3Ph520", "kxANrDshRUk")

    //    private val _youtubeList: MutableStateFlow<Result<YouTubeResponse>> = MutableStateFlow(Result.success(YouTubeResponse.EMPTY))
//    val youtubeList: StateFlow<Result<YouTubeResponse>> = _youtubeList
//    suspend fun setYoutubeList(youTubeResponse: Result<YouTubeResponse>){
//        _youtubeList.emit(youTubeResponse)
//    }
    private var _youtubeList: List<YoutubeDTO> = _youtubeIdList.map {
        runBlocking(Dispatchers.IO) {
            YouTubeUtils.getYoutubeDto(it)
        }
    }
    val youtubeList: List<YoutubeDTO> = _youtubeList

    private val _profileInfo = MutableStateFlow<Profile?>(null)
    val profileInfo: StateFlow<Profile?> = _profileInfo.asStateFlow()


    fun setYoutubeList(youtubeList: List<YoutubeDTO>) {
        _youtubeList = youtubeList
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _youtubeList =_youtubeIdList.map {videoId ->
                val url = "https://www.youtube.com/watch?v=$videoId"
                val doc = Jsoup.connect(url).get()
                val title = doc.select("meta[name=title]").first()?.attr("content") ?: "제목을 찾을 수 없습니다."
                YoutubeDTO(
                    title = title,
                    isVisible = false,
                    link = "https://www.youtube.com/watch?v=$videoId",
                    thumbnail = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg",
                    alternativeThumbnail = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg",
                )
            }
        }

    }

    suspend fun getYoutubeList(): List<YoutubeDTO> {
        return youtubeList
    }

    suspend fun getProfileInfo() {
        val result = getProfileInfoUseCase().getOrThrow()
        _profileInfo.value = Profile(profileUrl = result.data.profileUrl, name = result.data.name)
    }
}