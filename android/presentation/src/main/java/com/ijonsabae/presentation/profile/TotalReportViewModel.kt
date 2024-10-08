package com.ijonsabae.presentation.profile

import androidx.lifecycle.ViewModel
import com.ijonsabae.domain.model.TotalReport
import com.ijonsabae.domain.usecase.profile.GetTotalReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private const val TAG = "굿샷_TotalReportViewModel"

@HiltViewModel
class TotalReportViewModel @Inject constructor(
    private val getTotalReportUseCase: GetTotalReportUseCase
) : ViewModel() {

    private val _totalReport = MutableStateFlow<TotalReport?>(null)
    val totalReport: StateFlow<TotalReport?> = _totalReport.asStateFlow()

    private val _error = MutableStateFlow<String?>("스윙 횟수가 적어 종합 리포트로 이동할 수 없습니다.\n16회 이상 스윙해주세요!")
    val error: StateFlow<String?> = _error.asStateFlow()

    suspend fun getTotalReport() {
        val result = getTotalReportUseCase().getOrThrow()
        if (result.code == 204) {
            _error.value = result.message
        } else {
            _totalReport.value = result.data
        }
    }

}