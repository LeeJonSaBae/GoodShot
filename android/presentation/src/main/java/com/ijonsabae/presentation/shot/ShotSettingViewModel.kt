package com.ijonsabae.presentation.shot

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ShotSettingViewModel @Inject constructor(): ViewModel() {
    private val _isLeft: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLeft: StateFlow<Boolean>
        get() = _isLeft

    suspend fun setIsLeftStatus(isLeft: Boolean){
        _isLeft.emit(isLeft)
    }

    private val _showMidReport: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showMidReport: StateFlow<Boolean>
        get() = _showMidReport

    suspend fun setShowMidReportStatus(status: Boolean){
        _showMidReport.emit(status)
    }

    private val _showCnt: MutableStateFlow<Int> = MutableStateFlow(0)
    val showCnt: StateFlow<Int>
        get() = _showCnt

    suspend fun setShotCnt(cnt: Int){
        _showCnt.emit(cnt)
    }
}