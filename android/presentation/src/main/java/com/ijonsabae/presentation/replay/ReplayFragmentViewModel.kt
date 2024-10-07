package com.ijonsabae.presentation.replay

import android.util.Log
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val TAG = "ReplayFragmentViewModel_싸피"
@HiltViewModel
class ReplayFragmentViewModel @Inject constructor(
    private val getLocalSwingFeedbackListUseCase: GetLocalSwingFeedbackListUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val getLocalSwingFeedbackLikeListUseCase: GetLocalSwingFeedbackLikeListUseCase
): ViewModel() {
    private val _id = runBlocking { getUserIdUseCase() }
    var swingFeedbackList : StateFlow<PagingData<SwingFeedback>> = runBlocking { getLocalSwingFeedbackListUseCase(getUserID()).cachedIn(viewModelScope).stateIn(viewModelScope) }
    fun getUserID(): Long{
        return runBlocking {
            getUserIdUseCase()
        }
    }
    fun getLocalSwingFeedbackLikeList(){
        Log.d(TAG, "getLocalSwingFeedbackLikeList: ${getUserID()}")
        swingFeedbackList = runBlocking { getLocalSwingFeedbackLikeListUseCase(getUserID()).cachedIn(viewModelScope).stateIn(viewModelScope) }
    }
    fun getLocalSwingFeedbackList(){
        swingFeedbackList = runBlocking { getLocalSwingFeedbackListUseCase(getUserID()).cachedIn(viewModelScope).stateIn(viewModelScope) }
    }
}