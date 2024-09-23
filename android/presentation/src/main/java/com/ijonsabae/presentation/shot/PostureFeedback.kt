package com.ijonsabae.presentation.shot

import android.graphics.Bitmap
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

    fun checkPosture(sequentialPoseData: List<List<Pair<Bitmap, List<KeyPoint>>>>): MutableList<PoseAnalysisResult> {
        val feedbackList = mutableListOf<PoseAnalysisResult>()
        val poseChecks = listOf<(List<KeyPoint>) -> List<Feedback>>(
            ::checkAddress,
            ::checkToeUp,
            ::checkMidBackSwing,
            ::checkTop,
            ::checkMidDownSwing,
            ::checkImpact,
            ::checkMidFollowThrough,
            ::checkFinish
        )

        // 8개의 포즈 데이터에 대해 반복
        sequentialPoseData.forEachIndexed { index, threeFrameData ->
            // 각 포즈의 3개 프레임을 모두 분석
            val results = threeFrameData.map { (bitmap, keyPoints) ->
                Pair(bitmap, poseChecks[index](keyPoints))
            }
            // 3개의 분석 결과 중에서 긍정적인 피드백이 가장 많고 부정적인 피드백이 가장 적은 결과를 선택
            val bestResult = results.maxWithOrNull { a, b ->
                val aNiceFeedbackCount = a.second.count { it is NiceFeedback }
                val bNiceFeedbackCount = b.second.count { it is NiceFeedback }
                when {
                    aNiceFeedbackCount != bNiceFeedbackCount -> aNiceFeedbackCount.compareTo(
                        bNiceFeedbackCount
                    )

                    else -> {
                        val aBadFeedbackCount = a.second.count { it is BadFeedback }
                        val bBadFeedbackCount = b.second.count { it is BadFeedback }
                        bBadFeedbackCount.compareTo(aBadFeedbackCount)
                    }
                }
            }
            // 가장 좋은 피드백 담아주기
            bestResult?.let { (bitmap, feedbacks) ->
                feedbackList.add(
                    PoseAnalysisResult(
                        Pose.entries[index],
                        bitmap,
                        feedbacks
                    )
                )
            }
        }

        return feedbackList
    }

    private fun checkAddress(keyPoints: List<KeyPoint>): List<Feedback> {
        return emptyList()
    }

    private fun checkToeUp(keyPoints: List<KeyPoint>): List<Feedback> {
        return emptyList()

    }

    private fun checkMidBackSwing(keyPoints: List<KeyPoint>): List<Feedback> {
        return emptyList()

    }

    private fun checkTop(keyPoints: List<KeyPoint>): List<Feedback> {
        return emptyList()

    }

    private fun checkMidDownSwing(keyPoints: List<KeyPoint>): List<Feedback> {
        return emptyList()

    }

    private fun checkImpact(keyPoints: List<KeyPoint>): List<Feedback> {
        return emptyList()

    }

    private fun checkMidFollowThrough(keyPoints: List<KeyPoint>): List<Feedback> {
        return emptyList()

    }

    // 피니쉬 자세 판별 함수
    private fun checkFinish(keyPoints: List<KeyPoint>): List<Feedback> {
        val feedbackList = mutableListOf<Feedback>()

        // 1. 머리 위치: 시선은 정면을 보고 뒤로 젖혀지지 않도록
        val nose = keyPoints.find { it.bodyPart == BodyPart.NOSE }?.coordinate
        val leftEye = keyPoints.find { it.bodyPart == BodyPart.LEFT_EYE }?.coordinate
        val rightEye = keyPoints.find { it.bodyPart == BodyPart.RIGHT_EYE }?.coordinate

        if (nose != null && leftEye != null && rightEye != null) {
            val eyeHeightDifference = abs(leftEye.y - rightEye.y)
            if (eyeHeightDifference > 0.05f) { // 눈의 높이가 많이 차이나면 머리가 기울어졌다고 판단
                feedbackList.add(BadFeedback("시선은 정면을 보고, 머리가 뒤로 젖혀지지 않도록 해주세요."))
            }

        }

        // 2. 왼쪽 어깨 위치: 과도하게 높아지지 않도록
        val leftShoulder = keyPoints.find { it.bodyPart == BodyPart.LEFT_SHOULDER }?.coordinate
        val rightShoulder = keyPoints.find { it.bodyPart == BodyPart.RIGHT_SHOULDER }?.coordinate

        if (leftShoulder != null && rightShoulder != null) {
            val shoulderHeightDifference = abs(leftShoulder.y - rightShoulder.y)
            if (shoulderHeightDifference > 0.1f) { // 어깨 높이 차이가 크면 피드백
                feedbackList.add(BadFeedback("왼쪽 어깨가 과도하게 높아지지 않도록 주의해주세요."))
            }
        }

        // 3. 왼쪽 다리: 굽혀지지 않도록 중심축 유지
        val leftKnee = keyPoints.find { it.bodyPart == BodyPart.LEFT_KNEE }?.coordinate
        val leftAnkle = keyPoints.find { it.bodyPart == BodyPart.LEFT_ANKLE }?.coordinate

        if (leftKnee != null && leftAnkle != null) {
            if (leftKnee.x - leftAnkle.x > 0.05f) { // 무릎이 발목보다 앞으로 나가 있으면 피드백
                feedbackList.add(BadFeedback("왼쪽 다리가 굽혀지지 않도록 중심축을 유지해주세요."))
            }
        }

        // 4. 몸 위치: 중심이 왼쪽 다리와 일직선
        val leftHip = keyPoints.find { it.bodyPart == BodyPart.LEFT_HIP }?.coordinate

        if (leftHip != null && leftKnee != null && leftAnkle != null) {
            val bodyAlignment = abs(leftHip.x - leftAnkle.x)
            if (bodyAlignment > 0.1f) { // 몸의 중심이 왼쪽 다리와 일직선이 아니면 피드백
                feedbackList.add(BadFeedback("몸의 중심이 왼쪽 다리 위치와 일직선이 되도록 해주세요."))
            }
        }

        // 5. 골반: 왼쪽 골반과 오른쪽 골반이 일직선
        val rightHip = keyPoints.find { it.bodyPart == BodyPart.RIGHT_HIP }?.coordinate

        if (leftHip != null && rightHip != null) {
            val hipAlignment = abs(leftHip.y - rightHip.y)
            if (hipAlignment > 0.05f) { // 골반이 기울어졌으면 피드백
                feedbackList.add(BadFeedback("왼쪽 골반과 오른쪽 골반이 일직선이 되도록 해주세요."))
            }
        }

        if (feedbackList.isEmpty()) {
            feedbackList.add(NiceFeedback("피니쉬 자세가 올바릅니다."))
        }

        return feedbackList
    }
}