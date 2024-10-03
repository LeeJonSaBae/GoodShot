package com.ijonsabae.presentation.consult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ijonsabae.domain.model.Expert
import com.ijonsabae.domain.usecase.consult.GetConsultantListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ConsultViewModel @Inject constructor(private val getConsultantListUseCase: GetConsultantListUseCase) : ViewModel() {
    val consultantList: Flow<PagingData<Expert>> = getConsultantListUseCase().cachedIn(viewModelScope).distinctUntilChanged()
}