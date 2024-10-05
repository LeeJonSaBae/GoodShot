package com.ijonsabae.presentation.replay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.usecase.login.GetUserIdUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ReplayFragmentViewModel @Inject constructor(
    private val getLocalSwingFeedbackListUseCase: GetLocalSwingFeedbackListUseCase,
    private val getUserIdUseCase: GetUserIdUseCase
): ViewModel() {
    private val _id = runBlocking { getUserIdUseCase() }
    val swingFeedbackList : Flow<PagingData<SwingFeedback>> = getLocalSwingFeedbackListUseCase(_id).cachedIn(viewModelScope)
}