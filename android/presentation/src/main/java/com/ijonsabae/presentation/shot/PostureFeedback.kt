package com.ijonsabae.presentation.shot


import android.graphics.PointF
import android.util.Log
import com.ijonsabae.presentation.shot.ai.data.BackSwingProblem
import com.ijonsabae.presentation.shot.ai.data.BackSwingProblem.*
import com.ijonsabae.presentation.shot.ai.data.BadComment
import com.ijonsabae.presentation.shot.ai.data.BodyPart.*
import com.ijonsabae.presentation.shot.ai.data.Comment
import com.ijonsabae.presentation.shot.ai.data.Direction
import com.ijonsabae.presentation.shot.ai.data.Direction.*
import com.ijonsabae.presentation.shot.ai.data.DownSwingProblem
import com.ijonsabae.presentation.shot.ai.data.KeyPoint
import com.ijonsabae.presentation.shot.ai.data.NiceComment
import com.ijonsabae.presentation.shot.ai.data.Pose
import com.ijonsabae.presentation.shot.ai.data.Pose.*
import com.ijonsabae.presentation.shot.ai.data.PoseAnalysisResult
import com.ijonsabae.presentation.shot.ai.data.Solution
import com.ijonsabae.presentation.shot.ai.data.Solution.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

object PostureFeedback {
    private lateinit var frameIndexes: List<Int>
    private lateinit var jointList: List<List<KeyPoint>>
    private var isRightHanded: Boolean = true

    fun checkPosture(
        frameIndex: List<Int>,
        jointList: List<List<KeyPoint>>, // 피니쉬부터 역순으로  관절 좌표가 들어있음
        isRightHanded: Boolean
    ): PoseAnalysisResult {
        this.frameIndexes = frameIndex
        this.jointList = jointList
        this.isRightHanded = isRightHanded

        val backSwingProblems = checkBackSwing()
        val downSwingProblems = checkDownSwing()
        val analysisResult = determineSolutionAndComment(backSwingProblems, downSwingProblems)
        return PoseAnalysisResult(analysisResult, backSwingProblems, downSwingProblems)
    }

    private fun determineSolutionAndComment(
        backSwingProblems: List<Comment>,
        downSwingProblems: List<Comment>
    ): Solution {
        var solution: Solution? = null
        // BackSwing - 상체들림(0,4) 익스텐션(2) 체중이동(3) 무게중심(1)
        if (backSwingProblems[0] is BadComment || backSwingProblems[4] is BadComment) {
            solution = BACK_BODY_LIFT
        } else if (backSwingProblems[2] is BadComment) {
            solution = BACK_ARM_EXTENSION
        } else if (backSwingProblems[3] is BadComment) {
            solution = BACK_WEIGHT_TRANSFER
        } else if (backSwingProblems[1] is BadComment) {
            solution = BACK_BODY_BALANCE
        }

        // DownSwing - 상체들림(0,2) 체중이동(3,4) 무게중심(1)
        if (solution == null) {
            solution =
                if (downSwingProblems[0] is BadComment || downSwingProblems[2] is BadComment) {
                    DOWN_BODY_LIFT
                } else if (downSwingProblems[3] is BadComment || downSwingProblems[4] is BadComment) {
                    DOWN_WEIGHT_TRANSFER
                } else if (downSwingProblems[1] is BadComment) {
                    DOWN_BODY_BALANCE
                } else {
                    GOOD_SHOT
                }
        }
        return solution
    }

    private fun checkBackSwing(): List<Comment> {
        val analysisList = mutableListOf<Comment>()

        // 1. 백스윙 프레임에서 머리 고정 좌표 체크 - 상체가 들리거나 숙여지는지
        analysisList.add(checkBackSwingHeadMovement())

        // 2. 백스윙 프레임에서 골반 틀어짐 체크 - 명치만 돌아가고 골반은 가만히 있어야 함
        analysisList.add(checkBackSwingHipRotation())

        // 3. 어드레스 to 테이크 어웨이 왼팔이 펴져 있는지 + 시선 체크? - 일정한 스윙 궤도 유지를 위함
        analysisList.add(checkBackSwingArmBend())

        // 4. 백스윙 프레임에서 무릎이 살짝 오른쪽으로 쏠리는지 체크 - 체중이동
        analysisList.add(checkBackSwingKneeSway())

        // 5. 탑스윙에서 앞발이 뜨는지 어드레스와 비교 - 상체가 들리는지
        analysisList.add(checkBackSwingFrontFootLift())

        return analysisList
    }

    private fun checkDownSwing(): List<Comment> {
        val analysisResult = mutableListOf<Comment>()

        // 1. 머리 고정 좌표 체크 - 상체가 들리거나 숙여지는지
        analysisResult.add(checkDownSwingHeadMovement())

        // 2. 골반 높이가 임팩트까지 일정하게 유지되는지 체크
        analysisResult.add(checkDownSwingHipHeight())

        // 3.임팩트시 손 높이가 어드레스와 일치하는지 체크 - 상체가 들리거나 숙여지는지
        analysisResult.add(checkDownSwingImpactHandHeight())

        // 4. 무릎이 살짝 왼쪽으로 쏠리는지 체크 - 체중이동
        analysisResult.add(checkDownSwingKneeSway())

        // 5. 임팩트 시점에 머리(코)가 공(다리중심)보다 왼쪽에 위치 - 체중이동
        analysisResult.add(checkDownSwingHeadPositionImpact())

        return analysisResult
    }

    private fun checkDownSwingHeadPositionImpact(): Comment {
        val impactFrame = frameIndexes[IMPACT.ordinal]

        val noseX = jointList[impactFrame][NOSE.ordinal].coordinate.x
        val leftHipX = jointList[impactFrame][LEFT_HIP.ordinal].coordinate.x
        val rightHipX = jointList[impactFrame][RIGHT_HIP.ordinal].coordinate.x

        val hipCenterX = (leftHipX + rightHipX) / 2

        val headOffset = hipCenterX - noseX
        val leftThreshold = 0.08f  // 코가 골반 중심보다 왼쪽에 있어야 하는 최소 거리
        val rightThreshold = 0f

        val isProblem =
            headOffset > leftThreshold || headOffset < rightThreshold // 머리가 공보다 너무 앞이나 뒤에 있으면 문제

        val deviationDirection = if (headOffset > 0) BACK else FRONT

        val problem = if (isRightHanded)
            DownSwingProblem.RIGHT_HEAD_POSITION_IMPACT
        else
            DownSwingProblem.LEFT_HEAD_POSITION_IMPACT

        val comment = if (isProblem) {
            BadComment(problem.getBadComment(deviationDirection))
        } else {
            NiceComment(problem.getNiceComment())
        }

        return comment
    }

    private fun checkDownSwingKneeSway(): Comment {
        val leadingKneeIndex = LEFT_KNEE.ordinal
        val trailingKneeIndex = RIGHT_KNEE.ordinal

        val initialLeadingKneeX =
            jointList[frameIndexes[Pose.TOP.ordinal]][leadingKneeIndex].coordinate.x
        val initialTrailingKneeX =
            jointList[frameIndexes[Pose.TOP.ordinal]][trailingKneeIndex].coordinate.x

        val finalLeadingKneeX =
            jointList[frameIndexes[IMPACT.ordinal]][leadingKneeIndex].coordinate.x
        val finalTrailingKneeX =
            jointList[frameIndexes[IMPACT.ordinal]][trailingKneeIndex].coordinate.x

        val leadingKneeSway = finalLeadingKneeX - initialLeadingKneeX
        val trailingKneeSway = finalTrailingKneeX - initialTrailingKneeX

        val totalSway = leadingKneeSway + trailingKneeSway
        val threshold = 0.04f  // 무릎 이동을 문제로 간주할 임계값

        val isProblem = totalSway < threshold

        val problem =
            if (isRightHanded) DownSwingProblem.RIGHT_KNEE_SWAY else DownSwingProblem.LEFT_KNEE_SWAY
        val comment = if (isProblem) {
            BadComment(problem.getBadComment(CENTER)) // 방향은 좌타, 우타에 따라 이미 결정되어 있음
        } else {
            NiceComment(problem.getNiceComment())
        }
        return comment
    }

    private fun checkDownSwingImpactHandHeight(): Comment {
        val addressHandY = jointList[frameIndexes[ADDRESS.ordinal]][LEFT_WRIST.ordinal].coordinate.y
        val impactHandY = jointList[frameIndexes[IMPACT.ordinal]][LEFT_WRIST.ordinal].coordinate.y

        val heightDifference = impactHandY - addressHandY
        val threshold = 0.2f  // 손 높이 차이를 문제로 간주할 임계값

        val isProblem = abs(heightDifference) > threshold
        val deviationDirection = if (heightDifference > 0) BOTTOM else Direction.TOP

        val problem =
            if (isRightHanded) DownSwingProblem.RIGHT_IMPACT_HAND_HEIGHT else DownSwingProblem.LEFT_IMPACT_HAND_HEIGHT
        val comment = if (isProblem) {
            BadComment(problem.getBadComment(deviationDirection))
        } else {
            NiceComment(problem.getNiceComment())
        }

        return comment
    }

    private fun checkDownSwingHipHeight(): Comment {
        val initialLeftHipY =
            jointList[frameIndexes[Pose.TOP.ordinal]][LEFT_HIP.ordinal].coordinate.y
        val initialRightHipY =
            jointList[frameIndexes[Pose.TOP.ordinal]][RIGHT_HIP.ordinal].coordinate.y
        val initialHipHeight = (initialLeftHipY + initialRightHipY) / 2

        var isProblem = false
        val threshold = 0.08f  // 골반 높이 변화를 문제로 간주할 임계값
        var deviationDirection: Direction = CENTER

        for (frameIndex in frameIndexes[Pose.TOP.ordinal] downTo frameIndexes[IMPACT.ordinal]) {
            val currentLeftHipY = jointList[frameIndex][LEFT_HIP.ordinal].coordinate.y
            val currentRightHipY = jointList[frameIndex][RIGHT_HIP.ordinal].coordinate.y
            val currentHipHeight = (currentLeftHipY + currentRightHipY) / 2

            val heightDifference = currentHipHeight - initialHipHeight

            if (abs(heightDifference) > threshold) {
                isProblem = true
                deviationDirection = if (heightDifference > 0) BOTTOM else Direction.TOP
                break
            }
        }

        val problem =
            if (isRightHanded) DownSwingProblem.RIGHT_HIP_HEIGHT else DownSwingProblem.LEFT_HIP_HEIGHT
        val comment = if (isProblem) {
            BadComment(problem.getBadComment(deviationDirection))
        } else {
            NiceComment(problem.getNiceComment())
        }
        return comment
    }

    private fun checkDownSwingHeadMovement(): Comment {
        val initialNoseCoordinate =
            jointList[frameIndexes[Pose.TOP.ordinal]][NOSE.ordinal].coordinate
        var isProblem = false
        val threshold = 0.08f
        var deviationDirection: Direction = CENTER

        for (frameIndex in frameIndexes[Pose.TOP.ordinal] downTo frameIndexes[IMPACT.ordinal]) {
            val noseCoordinate = jointList[frameIndex][NOSE.ordinal].coordinate
            val distance = calculateDistance(initialNoseCoordinate, noseCoordinate)
            if (distance > threshold) {
                isProblem = true
                deviationDirection = determineDirection(initialNoseCoordinate, noseCoordinate)
                break
            }
        }

        val problem =
            if (isRightHanded) DownSwingProblem.RIGHT_HEAD_MOVEMENT else DownSwingProblem.LEFT_HEAD_MOVEMENT
        val comment = if (isProblem) BadComment(problem.getBadComment(deviationDirection))
        else NiceComment(problem.getNiceComment())
        return comment
    }

    private fun checkBackSwingFrontFootLift(): Comment {
        val ankleIndex = LEFT_ANKLE.ordinal

        val initialAnkleY = jointList[frameIndexes[ADDRESS.ordinal]][ankleIndex].coordinate.y
        var isProblem = false
        val threshold = 0.02f  // 발목이 들렸다고 간주할 높이 차이의 임계값, 필요에 따라 조정 가능

        for (frameIndex in frameIndexes[ADDRESS.ordinal] downTo frameIndexes[Pose.TOP.ordinal]) {
            val currentAnkleY = jointList[frameIndex][ankleIndex].coordinate.y
            val liftDistance = initialAnkleY - currentAnkleY  // y 좌표가 작아질수록 높이가 올라감
            if (liftDistance > threshold) {
                isProblem = true
                break
            }
        }

        val problem = if (isRightHanded) RIGHT_FRONT_FOOT_LIFT else LEFT_FRONT_FOOT_LIFT
        val comment = if (isProblem) {
            BadComment(problem.getBadComment(CENTER))  // 방향은 필요 없으므로 CENTER 사용
        } else {
            NiceComment(problem.getNiceComment())
        }
        return comment
    }

    private fun checkBackSwingKneeSway(): Comment {
        val leadingKneeIndex = RIGHT_KNEE.ordinal
        val trailingKneeIndex = LEFT_KNEE.ordinal

        val initialLeadingKneeX =
            jointList[frameIndexes[ADDRESS.ordinal]][leadingKneeIndex].coordinate.x
        val initialTrailingKneeX =
            jointList[frameIndexes[ADDRESS.ordinal]][trailingKneeIndex].coordinate.x

        val finalLeadingKneeX =
            jointList[frameIndexes[Pose.TOP.ordinal]][leadingKneeIndex].coordinate.x
        val finalTrailingKneeX =
            jointList[frameIndexes[Pose.TOP.ordinal]][trailingKneeIndex].coordinate.x

        val leadingKneeSway = initialLeadingKneeX - finalLeadingKneeX
        val trailingKneeSway = initialTrailingKneeX - finalTrailingKneeX

        val totalSway = leadingKneeSway + trailingKneeSway
        val threshold = 0.012f  // 무릎 이동을 문제로 간주할 임계값

        val isProblem = totalSway < threshold

        val problem =
            if (isRightHanded) RIGHT_KNEE_SWAY else LEFT_KNEE_SWAY
        val comment = if (isProblem) {
            BadComment(problem.getBadComment(CENTER)) // 방향은 좌타, 우타에 따라 이미 결정되어 있음
        } else {
            NiceComment(problem.getNiceComment())
        }
        return comment
    }

    private fun checkBackSwingArmBend(): Comment {
        val shoulderIndex = LEFT_SHOULDER.ordinal
        val elbowIndex = LEFT_ELBOW.ordinal
        val wristIndex = LEFT_WRIST.ordinal

        var isProblem = false
        val threshold = 22f  // 팔이 구부러진 것으로 간주할 각도 임계값

        for (frameIndex in frameIndexes[ADDRESS.ordinal] downTo frameIndexes[TOE_UP.ordinal]) {
            val shoulder = jointList[frameIndex][shoulderIndex].coordinate
            val elbow = jointList[frameIndex][elbowIndex].coordinate
            val wrist = jointList[frameIndex][wristIndex].coordinate

            val externalAngle = calculateAngle(shoulder, elbow, wrist)
            val bendAngle = abs(180f - externalAngle)

            if (bendAngle > threshold) {
                isProblem = true
                break
            }
        }

        val problem = if (isRightHanded) RIGHT_ARM_BEND else LEFT_ARM_BEND
        val comment = if (isProblem) {
            BadComment(problem.getBadComment(CENTER))  // 방향은 필요 없으므로 CENTER 사용
        } else {
            NiceComment(problem.getNiceComment())
        }
        return comment
    }

    private fun checkBackSwingHipRotation(): Comment {
        val initialLeftHipY =
            jointList[frameIndexes[ADDRESS.ordinal]][LEFT_HIP.ordinal].coordinate.y
        val initialRightHipY =
            jointList[frameIndexes[ADDRESS.ordinal]][RIGHT_HIP.ordinal].coordinate.y
        val initialHipDifference = abs(initialLeftHipY - initialRightHipY)

        var isProblem = false
        val threshold = 0.05f  // 힙 회전을 판단하기 위한 임계값, 필요에 따라 조정 가능

        for (frameIndex in frameIndexes[ADDRESS.ordinal] downTo frameIndexes[Pose.TOP.ordinal]) {
            val currentLeftHipY = jointList[frameIndex][LEFT_HIP.ordinal].coordinate.y
            val currentRightHipY = jointList[frameIndex][RIGHT_HIP.ordinal].coordinate.y
            val currentHipDifference = abs(currentLeftHipY - currentRightHipY)

            if (abs(currentHipDifference - initialHipDifference) > threshold) {
                isProblem = true
                break
            }
        }

        val problem = if (isRightHanded) RIGHT_HIP_ROTATION else LEFT_HIP_ROTATION
        return if (isProblem) {
            BadComment(problem.getBadComment(CENTER))  // 방향은 필요 없으므로 CENTER 사용
        } else {
            NiceComment(problem.getNiceComment())
        }
    }

    private fun checkBackSwingHeadMovement(): Comment {
        val initialNoseCoordinate =
            jointList[frameIndexes[ADDRESS.ordinal]][NOSE.ordinal].coordinate
        var isProblem = false
        val threshHold = 0.12f
        var deviationDirection: Direction = CENTER

        for (frameIndex in frameIndexes[ADDRESS.ordinal] downTo frameIndexes[Pose.TOP.ordinal]) {
            val noseCoordinate = jointList[frameIndex][NOSE.ordinal].coordinate
            val distance = calculateDistance(initialNoseCoordinate, noseCoordinate)
            if (distance > threshHold) {
                isProblem = true
                deviationDirection = determineDirection(initialNoseCoordinate, noseCoordinate)
                break;
            }
        }
        val problem = if (isRightHanded) RIGHT_HEAD_MOVEMENT else LEFT_HEAD_MOVEMENT
        val comment = if (isProblem) BadComment(problem.getBadComment(deviationDirection))
        else NiceComment(problem.getNiceComment())
        return comment
    }

    private fun determineDirection(initial: PointF, current: PointF): Direction {
        val dx = current.x - initial.x
        val dy = current.y - initial.y

        return when {
            kotlin.math.abs(dx) > kotlin.math.abs(dy) -> if (dx > 0) RIGHT else LEFT
            else -> if (dy > 0) BOTTOM else Direction.TOP
        }
    }

    private fun calculateDistance(p1: PointF, p2: PointF): Float {
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }

    // p1 -> p2 -> p3가 이루는 각을 반시계 방향으로 측정
    private fun calculateAngle(p1: PointF, p2: PointF, p3: PointF): Float {
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