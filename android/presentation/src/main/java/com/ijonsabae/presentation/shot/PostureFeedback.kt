package com.ijonsabae.presentation.shot

import android.graphics.Bitmap
import android.graphics.PointF
import android.util.Log
import com.ijonsabae.presentation.shot.ai.data.BackSwingProblem
import com.ijonsabae.presentation.shot.ai.data.BackSwingProblem.*
import com.ijonsabae.presentation.shot.ai.data.BadFeedback
import com.ijonsabae.presentation.shot.ai.data.BodyPart
import com.ijonsabae.presentation.shot.ai.data.BodyPart.*
import com.ijonsabae.presentation.shot.ai.data.Feedback


import com.ijonsabae.presentation.shot.ai.data.KeyPoint
import com.ijonsabae.presentation.shot.ai.data.NiceFeedback
import com.ijonsabae.presentation.shot.ai.data.Pose
import com.ijonsabae.presentation.shot.ai.data.Pose.*
import com.ijonsabae.presentation.shot.ai.data.PoseAnalysisResult
import java.util.Queue
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

object PostureFeedback {

    private lateinit var preciseFrameIndex: List<Int>
    private lateinit var jointList: List<List<KeyPoint>>

    fun checkPosture(
        preciseFrameIndex: List<Int>,
        jointList: List<List<KeyPoint>> // 피니쉬부터 역순으로  관절 좌표가 들어있음
    ): PoseAnalysisResult {
        this.preciseFrameIndex = preciseFrameIndex
        this.jointList = jointList
        return PoseAnalysisResult(checkBackSwing(), checkDownSwing())
    }

    private fun checkBackSwing(): List<Feedback> {
        val feedbackList = mutableListOf<Feedback>()

        // 1. [STRAIGHT_ELBOW] 어드레스부터 탑스윙까지 왼팔꿈치가 펴져 있는지 확인
        var isArmStraight = true
        for (i in preciseFrameIndex[ADDRESS.ordinal] downTo preciseFrameIndex[TOP.ordinal]) {
            val jointCoordinate = jointList[i]

            val leftShoulder = jointCoordinate[LEFT_SHOULDER.position].coordinate
            val leftElbow = jointCoordinate[LEFT_ELBOW.position].coordinate
            val leftWrist = jointCoordinate[LEFT_WRIST.position].coordinate

            val angle = calculateAngle(leftShoulder, leftElbow, leftWrist)
            // 허용 오차를 15도로 설정 (180도에서 ±15도 미만을 허용)
            if (abs(angle - 180) > 15) {
                isArmStraight = false
                break
            }
        }

        if (!isArmStraight) {
            feedbackList.add(
                BadFeedback(
                    problem = STRAIGHT_ELBOW.problem,
                    solution = STRAIGHT_ELBOW.solution
                )
            )
        } else {
            feedbackList.add(NiceFeedback(STRAIGHT_ELBOW.compliment))
        }

        return feedbackList
    }

    private fun checkDownSwing(
    ): List<Feedback> {
        val feedbackList = mutableListOf<Feedback>()

        return feedbackList
    }

    fun calculateAngle(p1: PointF, p2: PointF, p3: PointF): Float {
        val angle1 = atan2(p1.y - p2.y, p1.x - p2.x)
        val angle2 = atan2(p3.y - p2.y, p3.x - p2.x)
        var angle = angle2 - angle1

        // 각도를 0에서 2π 사이의 값으로 정규화
        if (angle < 0) {
            angle += 2 * PI.toFloat()
        }

        // 라디안을 도(degree)로 변환
        return (angle * 180 / PI).toFloat()
    }


}