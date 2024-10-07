package com.ijonsabae.presentation.shot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShotSettingViewModel @Inject constructor() : ViewModel() {
    private val _isLeft: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLeft: StateFlow<Boolean>
        get() = _isLeft

    private val _selectedGolfClub = MutableStateFlow("아이언")
    val selectedGolfClub: StateFlow<String>
        get() = _selectedGolfClub.asStateFlow()

    suspend fun setIsLeftStatus(isLeft: Boolean) {
        _isLeft.emit(isLeft)
    }

    fun setSelectedGolfClub(club: String) {
        viewModelScope.launch {
            _selectedGolfClub.emit(club)
        }
    }

    private val _showMidReport: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showMidReport: StateFlow<Boolean>
        get() = _showMidReport

    suspend fun setShowMidReportStatus(status: Boolean) {
        _showMidReport.emit(status)
    }

    private val _totalSwingCnt: MutableStateFlow<Int> = MutableStateFlow(15)
    val totalSwingCnt: StateFlow<Int>
        get() = _totalSwingCnt

    suspend fun setTotalSwingCnt(cnt: Int) {
        _totalSwingCnt.emit(cnt)
    }
}