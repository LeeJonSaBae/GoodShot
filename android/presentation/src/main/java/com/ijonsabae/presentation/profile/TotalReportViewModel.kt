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

    suspend fun getTotalReport() {
        val result = getTotalReportUseCase().getOrThrow()
        _totalReport.value = result.data
    }

}