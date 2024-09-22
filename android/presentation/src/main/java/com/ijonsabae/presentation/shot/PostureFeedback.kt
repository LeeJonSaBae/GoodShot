package com.ijonsabae.presentation.shot

import com.ijonsabae.presentation.shot.ai.data.BodyPart



import com.ijonsabae.presentation.shot.ai.data.KeyPoint
import kotlin.math.abs

class PostureFeedback {

    // 피니쉬 자세 판별 함수
    fun checkFinishPosture(keyPoints: List<KeyPoint>): List<String> {
        val feedback = mutableListOf<String>()

        // 1. 머리 위치: 시선은 정면을 보고 뒤로 젖혀지지 않도록
        val nose = keyPoints.find { it.bodyPart == BodyPart.NOSE }?.coordinate
        val leftEye = keyPoints.find { it.bodyPart == BodyPart.LEFT_EYE }?.coordinate
        val rightEye = keyPoints.find { it.bodyPart == BodyPart.RIGHT_EYE }?.coordinate

        if (nose != null && leftEye != null && rightEye != null) {
            val eyeHeightDifference = abs(leftEye.y - rightEye.y)
            if (eyeHeightDifference > 0.05f) { // 눈의 높이가 많이 차이나면 머리가 기울어졌다고 판단
                feedback.add("시선은 정면을 보고, 머리가 뒤로 젖혀지지 않도록 해주세요.")
            } 
        }

        // 2. 왼쪽 어깨 위치: 과도하게 높아지지 않도록
        val leftShoulder = keyPoints.find { it.bodyPart == BodyPart.LEFT_SHOULDER }?.coordinate
        val rightShoulder = keyPoints.find { it.bodyPart == BodyPart.RIGHT_SHOULDER }?.coordinate

        if (leftShoulder != null && rightShoulder != null) {
            val shoulderHeightDifference = abs(leftShoulder.y - rightShoulder.y)
            if (shoulderHeightDifference > 0.1f) { // 어깨 높이 차이가 크면 피드백
                feedback.add("왼쪽 어깨가 과도하게 높아지지 않도록 주의해주세요.")
            }
        }

        // 3. 왼쪽 다리: 굽혀지지 않도록 중심축 유지
        val leftKnee = keyPoints.find { it.bodyPart == BodyPart.LEFT_KNEE }?.coordinate
        val leftAnkle = keyPoints.find { it.bodyPart == BodyPart.LEFT_ANKLE }?.coordinate

        if (leftKnee != null && leftAnkle != null) {
            if (leftKnee.x - leftAnkle.x > 0.05f) { // 무릎이 발목보다 앞으로 나가 있으면 피드백
                feedback.add("왼쪽 다리가 굽혀지지 않도록 중심축을 유지해주세요.")
            }
        }

        // 4. 몸 위치: 중심이 왼쪽 다리와 일직선
        val leftHip = keyPoints.find { it.bodyPart == BodyPart.LEFT_HIP }?.coordinate

        if (leftHip != null && leftKnee != null && leftAnkle != null) {
            val bodyAlignment = abs(leftHip.x - leftAnkle.x)
            if (bodyAlignment > 0.1f) { // 몸의 중심이 왼쪽 다리와 일직선이 아니면 피드백
                feedback.add("몸의 중심이 왼쪽 다리 위치와 일직선이 되도록 해주세요.")
            }
        }

        // 5. 골반: 왼쪽 골반과 오른쪽 골반이 일직선
        val rightHip = keyPoints.find { it.bodyPart == BodyPart.RIGHT_HIP }?.coordinate

        if (leftHip != null && rightHip != null) {
            val hipAlignment = abs(leftHip.y - rightHip.y)
            if (hipAlignment > 0.05f) { // 골반이 기울어졌으면 피드백
                feedback.add("왼쪽 골반과 오른쪽 골반이 일직선이 되도록 해주세요.")
            }
        }

        return if (feedback.isEmpty()) {
            listOf("피니쉬 자세가 올바릅니다.")
        } else {
            feedback
        }
    }
}