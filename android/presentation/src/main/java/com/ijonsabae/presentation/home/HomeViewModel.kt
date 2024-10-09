package com.ijonsabae.presentation.home

import androidx.lifecycle.ViewModel
import com.ijonsabae.domain.model.Profile
import com.ijonsabae.domain.usecase.home.GetSearchVideosUseCase
import com.ijonsabae.domain.usecase.profile.GetProfileInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
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

    suspend fun getYoutubeList(): List<YoutubeDTO> {
        return _youtubeIdList.map {
            YouTubeUtils.getYoutubeDto(it)
        }
    }

    suspend fun getProfileInfo() {
        val result = getProfileInfoUseCase().getOrThrow()
        _profileInfo.value = Profile(profileUrl = result.data.profileUrl, name = result.data.name)
    }
}