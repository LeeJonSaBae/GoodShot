package com.ijonsabae.presentation.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ijonsabae.domain.model.Profile
import com.ijonsabae.domain.usecase.home.GetSearchVideosUseCase
import com.ijonsabae.domain.usecase.profile.GetProfileInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
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
    private var _youtubeList: MutableStateFlow<List<YoutubeDTO>> = MutableStateFlow(listOf())
    val youtubeList: StateFlow<List<YoutubeDTO>> = _youtubeList

    private val _profileInfo = MutableStateFlow<Profile?>(null)
    val profileInfo: StateFlow<Profile?> = _profileInfo.asStateFlow()


//    fun setYoutubeList(youtubeList: List<YoutubeDTO>) {
//        _youtubeList = youtubeList
//    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork?.let { network ->
            connectivityManager.getNetworkCapabilities(network)
        }
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

    }

    suspend fun load(){
            _youtubeList.emit(_youtubeIdList.map {videoId ->
                withContext(Dispatchers.IO) {
                    val url = "https://www.youtube.com/watch?v=$videoId"
                    val doc = Jsoup.connect(url).get()
                    val client = OkHttpClient()
                    val request = Request.Builder().url(url).build()
                    val response: Response = client.newCall(request).execute()
                    val html = response.body?.string()

                    // 정규 표현식을 사용하여 제목을 추출합니다.
                    val titleRegex = "<title>(.*?) - YouTube</title>".toRegex()
                    val matchResult = titleRegex.find(html ?: "")
                    val title = matchResult?.groups?.get(1)?.value?.trim()
                        ?: "제목이 없습니다!"
                    YoutubeDTO(
                        title = title,
                        isVisible = false,
                        link = "https://www.youtube.com/watch?v=$videoId",
                        thumbnail = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg",
                        alternativeThumbnail = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg",
                    )
                }
            }
            )
    }

//    suspend fun getYoutubeList(): List<YoutubeDTO> {
//        return youtubeList
//    }

    suspend fun getProfileInfo() {
        val result = getProfileInfoUseCase().getOrThrow()
        _profileInfo.value = Profile(profileUrl = result.data.profileUrl, name = result.data.name)
    }
}