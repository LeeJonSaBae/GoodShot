package com.ijonsabae.presentation.shot

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ijonsabae.presentation.shot.CameraState.ANALYZING
import com.ijonsabae.presentation.shot.CameraState.POSITIONING
import com.ijonsabae.presentation.shot.CameraState.RESULT

class SwingViewModel : ViewModel() {
    private val _currentState: MutableLiveData<CameraState> = MutableLiveData(ANALYZING)
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

    private fun isMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()
}