package com.ijonsabae.presentation.shot

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ijonsabae.presentation.model.FeedBack
import com.ijonsabae.presentation.shot.CameraState.POSITIONING
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SwingViewModel @Inject constructor() : ViewModel() {
    private val _currentState: MutableLiveData<CameraState> = MutableLiveData(POSITIONING)

    val currentState: LiveData<CameraState>
        get() = _currentState

    private var _feedBack: FeedBack? = null
    fun setFeedBack(feedBack: FeedBack) {
        _feedBack = feedBack
    }
    fun getFeedBack(): FeedBack? = _feedBack
    fun setCurrentState(newState: CameraState) {
        if (isMainThread()) {
            _currentState.value = newState
        } else {
            _currentState.postValue(newState)
        }
    }

    private fun isMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()
}