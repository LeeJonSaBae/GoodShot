package com.ijonsabae.presentation.shot

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ijonsabae.presentation.shot.CameraState.POSITIONING
import com.ijonsabae.presentation.shot.ai.camera.SwingTiming

class SwingViewModel : ViewModel() {
    private val _currentState: MutableLiveData<CameraState> = MutableLiveData(POSITIONING)


    val currentState: LiveData<CameraState>
        get() = _currentState
    val isRight = true

    fun setCurrentState(newState: CameraState) {
        if (isMainThread()) {
            _currentState.value = newState
        } else {
            _currentState.postValue(newState)
        }
    }


    var backswingTimeText: String = ""
    var downswingTimeText: String = ""
    var tempoRatioText: String = ""

    fun updateSwingTiming(swingTiming: SwingTiming) {
        backswingTimeText = String.format("%.2f s", swingTiming.backswingTime / 1000.0)
        downswingTimeText = String.format("%.2f s", swingTiming.downswingTime / 1000.0)
        tempoRatioText = String.format("%.1f:1", swingTiming.tempoRatio)
    }

    private fun isMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()
}