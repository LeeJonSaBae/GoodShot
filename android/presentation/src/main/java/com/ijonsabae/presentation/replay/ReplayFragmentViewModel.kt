package com.ijonsabae.presentation.replay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.usecase.login.GetUserIdUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackLikeListUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ReplayFragmentViewModel @Inject constructor(
    private val getLocalSwingFeedbackListUseCase: GetLocalSwingFeedbackListUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val getLocalSwingFeedbackLikeListUseCase: GetLocalSwingFeedbackLikeListUseCase
): ViewModel() {
    private val _id = runBlocking { getUserIdUseCase() }
    var swingFeedbackList : Flow<PagingData<SwingFeedback>> = getLocalSwingFeedbackListUseCase(_id).cachedIn(viewModelScope)
    fun getUserID(): Long{
        return runBlocking {
            getUserIdUseCase()
        }
    }
    fun getLocalSwingFeedbackLikeList(){
        swingFeedbackList = getLocalSwingFeedbackLikeListUseCase(_id).cachedIn(viewModelScope)
    }
    fun getLocalSwingFeedbackList(){
        swingFeedbackList = getLocalSwingFeedbackListUseCase(_id).cachedIn(viewModelScope)
    }
}