package com.ijonsabae.presentation.shot

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ijonsabae.presentation.shot.CameraState.POSITIONING

class CameraViewModel : ViewModel() {
    private val _currentState: MutableLiveData<CameraState> = MutableLiveData(POSITIONING)
    val currentState: LiveData<CameraState> = _currentState

    fun setCurrentState(newState: CameraState) {
        if (isMainThread()) {
            _currentState.value = newState
        } else {
            _currentState.postValue(newState)
        }
    }

    private fun isMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()
}