package com.ijonsabae.presentation.shot

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ijonsabae.presentation.shot.CameraState.POSITIONING
import com.ijonsabae.presentation.shot.ai.camera.SwingTiming
import com.ijonsabae.presentation.shot.ai.data.BadFeedback
import com.ijonsabae.presentation.shot.ai.data.NiceFeedback
import com.ijonsabae.presentation.shot.ai.data.PoseAnalysisResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SwingViewModel @Inject constructor() : ViewModel() {
    private val _currentState: MutableLiveData<CameraState> = MutableLiveData(POSITIONING)


    val currentState: LiveData<CameraState>
        get() = _currentState

    private val poseAnalysisResults: MutableList<PoseAnalysisResult> = mutableListOf()

    fun setPoseAnalysisResults(result: List<PoseAnalysisResult>) {
        poseAnalysisResults.clear()
        poseAnalysisResults.addAll(result)
    }

    // 8개의 포즈 중에서 가장 문제인 포즈의 피드백을 반환, 만약 BadFeedback이 하나도 없으면 NiceFeedback이 가장 많은걸 반환
    fun getWorstPoseAnalysisResult(): PoseAnalysisResult? {
        return poseAnalysisResults.maxWithOrNull { a, b ->
            val aBadCount = a.feedbacks.count { it is BadFeedback }
            val bBadCount = b.feedbacks.count { it is BadFeedback }

            when {
                aBadCount > 0 || bBadCount > 0 -> aBadCount.compareTo(bBadCount)
                else -> {
                    val aNiceCount = a.feedbacks.count { it is NiceFeedback }
                    val bNiceCount = b.feedbacks.count { it is NiceFeedback }
                    aNiceCount.compareTo(bNiceCount)
                }
            }
        }
    }

    fun setCurrentState(newState: CameraState) {
        if (isMainThread()) {
            _currentState.value = newState
        } else {
            _currentState.postValue(newState)
        }
    }


    var backswingTimeText: String = ""
    var downswingTimeText: String = ""
    var tempoRatioText: String = ""

    fun updateSwingTiming(swingTiming: SwingTiming) {
        backswingTimeText = String.format("%.2f초", swingTiming.backswingTime / 1000.0)
        downswingTimeText = String.format("%.2f초", swingTiming.downswingTime / 1000.0)
        tempoRatioText = String.format("%.2f", swingTiming.tempoRatio)
    }

    private fun isMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()
}