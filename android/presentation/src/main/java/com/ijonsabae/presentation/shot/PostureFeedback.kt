package com.ijonsabae.presentation.shot

import android.graphics.Bitmap
import android.util.Log
import com.ijonsabae.presentation.shot.ai.data.BadFeedback
import com.ijonsabae.presentation.shot.ai.data.BodyPart
import com.ijonsabae.presentation.shot.ai.data.Feedback


import com.ijonsabae.presentation.shot.ai.data.KeyPoint
import com.ijonsabae.presentation.shot.ai.data.NiceFeedback
import com.ijonsabae.presentation.shot.ai.data.Pose
import com.ijonsabae.presentation.shot.ai.data.Pose.*
import com.ijonsabae.presentation.shot.ai.data.PoseAnalysisResult
import kotlin.math.abs

object PostureFeedback {

    // 24개의 프레임 사이사이 보면서 더 정확한 대표 8개 프레임 뽑기 + 5가지 정도 피드백 체크하기
    fun checkPosture(sequentialPoseData: List<List<List<KeyPoint>>>): List<PoseAnalysisResult> {
        val feedbackList = mutableListOf<PoseAnalysisResult>()



        return feedbackList
    }

}