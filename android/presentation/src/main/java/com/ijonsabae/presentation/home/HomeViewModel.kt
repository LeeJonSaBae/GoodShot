package com.ijonsabae.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ijonsabae.domain.model.YouTubeResponse
import com.ijonsabae.domain.usecase.home.GetSearchVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getSearchVideosUseCase: GetSearchVideosUseCase
): ViewModel(){
    private val _youtubeList: MutableSharedFlow<Result<YouTubeResponse>> = MutableSharedFlow() //= MutableStateFlow(Result.success(YouTubeResponse.EMPTY))
    val youtubeList: SharedFlow<Result<YouTubeResponse>> = _youtubeList
    suspend fun setYoutubeList(youTubeResponse: Result<YouTubeResponse>){
        _youtubeList.emit(youTubeResponse)
    }

    init {
        viewModelScope.launch() {
            setYoutubeList(getSearchVideosUseCase("snippet", "로리 맥글로이처럼 잘 하는 법", 5))
        }
    }
}