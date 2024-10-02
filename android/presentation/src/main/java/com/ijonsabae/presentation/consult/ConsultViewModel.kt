package com.ijonsabae.presentation.consult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ijonsabae.domain.model.Expert
import com.ijonsabae.domain.usecase.consult.GetConsultantListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsultViewModel @Inject constructor(private val getConsultantListUseCase: GetConsultantListUseCase) : ViewModel() {
    private val _consultantList: MutableStateFlow<PagingData<Expert>> = MutableStateFlow(
        PagingData.empty()
    )
    val consultantList: StateFlow<PagingData<Expert>>
        get() = _consultantList

    private suspend fun setConsultantList(consultantList: Flow<PagingData<Expert>>) {
        consultantList.cachedIn(viewModelScope)
            .distinctUntilChanged().collect{
                result ->
                _consultantList.emit(result)
            }
    }



    init {
        viewModelScope.launch {
            setConsultantList(load())
        }
    }

    private fun load() = getConsultantListUseCase()

}