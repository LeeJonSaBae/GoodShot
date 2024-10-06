package com.ijonsabae.presentation.shot

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.model.SwingFeedbackComment
import com.ijonsabae.domain.usecase.login.GetUserIdUseCase
import com.ijonsabae.domain.usecase.shot.InsertLocalSwingFeedbackCommentUseCase
import com.ijonsabae.domain.usecase.shot.InsertLocalSwingFeedbackUseCase
import com.ijonsabae.presentation.model.FeedBack
import com.ijonsabae.presentation.shot.CameraState.POSITIONING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SwingViewModel @Inject constructor(
    private val getUserIdUseCase: GetUserIdUseCase,
    private val insertLocalSwingFeedbackUseCase: InsertLocalSwingFeedbackUseCase,
    private val insertLocalSwingFeedbackCommentUseCase: InsertLocalSwingFeedbackCommentUseCase
) : ViewModel() {
    private val _id : Long = runBlocking { getUserIdUseCase() }

    private val _currentState: MutableLiveData<CameraState> = MutableLiveData(POSITIONING)
    val currentState: LiveData<CameraState>
        get() = _currentState

    private var _feedBack: FeedBack? = null

    private var _swingCnt: Int = 0

    fun getUserId(): Long{
        return _id
    }

    fun insertSwingFeedbackComment(swingFeedbackComment: SwingFeedbackComment){
        insertLocalSwingFeedbackCommentUseCase(swingFeedbackComment)
    }

    fun insertSwingFeedback(swingFeedback: SwingFeedback){
        insertLocalSwingFeedbackUseCase(swingFeedback)
    }

    fun setCurrentState(newState: CameraState) {
        if (isMainThread()) {
            _currentState.value = newState
        } else {
            _currentState.postValue(newState)
        }
    }

    fun initializeSwingCnt() {
        this._swingCnt = 0
    }

    fun increaseSwingCnt() {
        this._swingCnt += 1
    }

    fun getSwingCnt(): Int = _swingCnt

    fun setFeedBack(feedBack: FeedBack) {
        _feedBack = feedBack
    }

    fun getFeedBack(): FeedBack? = _feedBack

    private fun isMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()
}