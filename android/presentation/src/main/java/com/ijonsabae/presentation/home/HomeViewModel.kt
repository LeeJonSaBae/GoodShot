package com.ijonsabae.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ijonsabae.domain.model.YouTubeResponse
import com.ijonsabae.domain.usecase.home.GetSearchVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getSearchVideosUseCase: GetSearchVideosUseCase
): ViewModel(){
    private val _youtubeIdList = listOf("Rogr71lXTg0", "-gNimeR9U5g", "jbxnVBA0kB0", "N5FpZ3Ph520", "kxANrDshRUk")
//    private val _youtubeList: MutableStateFlow<Result<YouTubeResponse>> = MutableStateFlow(Result.success(YouTubeResponse.EMPTY))
//    val youtubeList: StateFlow<Result<YouTubeResponse>> = _youtubeList
//    suspend fun setYoutubeList(youTubeResponse: Result<YouTubeResponse>){
//        _youtubeList.emit(youTubeResponse)
//    }

    private val _youtubeList : List<YoutubeDTO> = _youtubeIdList.map {
        runBlocking(Dispatchers.IO) {
            YouTubeUtils.getYoutubeDto(it)
        }
    }
    val youtubeList: List<YoutubeDTO> = _youtubeList

    init {
//        viewModelScope.launch() {
//            setYoutubeList(getSearchVideosUseCase("snippet", "로리 맥글로이처럼 잘 하는 법", 5))
//        }
    }
}