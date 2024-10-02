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

    private val _totalSwingCnt: MutableStateFlow<Int> = MutableStateFlow(1)
    val totalSwingCnt: StateFlow<Int>
        get() = _totalSwingCnt

    suspend fun setTotalSwingCnt(cnt: Int){
        _totalSwingCnt.emit(cnt)
    }
}