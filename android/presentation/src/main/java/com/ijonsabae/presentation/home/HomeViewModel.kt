package com.ijonsabae.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ijonsabae.domain.model.YouTubeResponse
import com.ijonsabae.domain.usecase.home.GetSearchVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getSearchVideosUseCase: GetSearchVideosUseCase
): ViewModel(){
    private val _youtubeList: MutableStateFlow<YouTubeResponse> = MutableStateFlow(YouTubeResponse.EMPTY)
    val youtubeList: StateFlow<YouTubeResponse> = _youtubeList
    suspend fun setYoutubeList(youTubeResponse: YouTubeResponse){
        _youtubeList.emit(youTubeResponse)
    }

    init {
        viewModelScope.launch {
            _youtubeList.emit(getSearchVideosUseCase("snippet", "로리 맥글로이처럼 잘 하는 법", 5))
        }
    }
}