package com.ijonsabae.presentation.consult

import androidx.lifecycle.ViewModel
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.ExpertDetail
import com.ijonsabae.domain.usecase.consult.GetConsultantInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ConsultantDetailInfoDialogViewModel @Inject constructor(
    private val getConsultantInfoUseCase: GetConsultantInfoUseCase
) : ViewModel() {
    private val _id: MutableStateFlow<Int> = MutableStateFlow(-1)
    val id : SharedFlow<Int>
        get()= _id

    suspend fun setId(id: Int){
        _id.emit(id)
    }

    private val _expertDetailInfo: MutableStateFlow<ExpertDetail> = MutableStateFlow(ExpertDetail.EMPTY)
    val expertDetailInfo: StateFlow<ExpertDetail> = _expertDetailInfo

    suspend fun getExpertDetailInfo(id: Int): Result<CommonResponse<ExpertDetail>>{
        return getConsultantInfoUseCase(id)
    }

    suspend fun setExpertDetailInfo(expert: ExpertDetail){
        _expertDetailInfo.emit(expert)
    }
}