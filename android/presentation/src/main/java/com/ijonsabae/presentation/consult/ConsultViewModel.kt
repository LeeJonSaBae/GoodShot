package com.ijonsabae.presentation.consult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ijonsabae.domain.model.Consultant
import com.ijonsabae.domain.usecase.consult.GetConsultantListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsultViewModel @Inject constructor(private val getConsultantListUseCase: GetConsultantListUseCase) : ViewModel() {
    private val _consultantList: MutableStateFlow<List<Consultant>> = MutableStateFlow(listOf())
    val consultantList: StateFlow<List<Consultant>>
        get() = _consultantList

    private suspend fun setConsultantList(consultantList: List<Consultant>) {
        _consultantList.emit(consultantList)
    }



    init {
        viewModelScope.launch {
            setConsultantList(load().getOrThrow())
        }
    }

    private fun load() = getConsultantListUseCase()

}