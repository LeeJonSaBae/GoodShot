package com.ijonsabae.presentation.consult

import androidx.lifecycle.ViewModel
import com.ijonsabae.presentation.model.ExpertDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ConsultantDetailInfoDialogViewModel @Inject constructor() : ViewModel() {
    private val _expertDetailInfo: MutableStateFlow<ExpertDetail> = MutableStateFlow(ExpertDetail.EMPTY)
    val expertDetailInfo: StateFlow<ExpertDetail> = _expertDetailInfo

    suspend fun setExpertDetailInfo(expert: ExpertDetail){
        _expertDetailInfo.emit(expert)
    }
}