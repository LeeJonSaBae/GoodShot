package com.ijonsabae.presentation.shot.ai

import android.graphics.Bitmap
import android.util.Log
import com.ijonsabae.presentation.shot.ai.data.BodyPart.LEFT_ANKLE
import com.ijonsabae.presentation.shot.ai.data.KeyPoint
import com.ijonsabae.presentation.shot.ai.data.BodyPart.LEFT_ELBOW
import com.ijonsabae.presentation.shot.ai.data.BodyPart.LEFT_HIP
import com.ijonsabae.presentation.shot.ai.data.BodyPart.LEFT_SHOULDER
import com.ijonsabae.presentation.shot.ai.data.BodyPart.LEFT_WRIST
import com.ijonsabae.presentation.shot.ai.data.BodyPart.NOSE
import com.ijonsabae.presentation.shot.ai.data.BodyPart.RIGHT_ANKLE
import com.ijonsabae.presentation.shot.ai.data.BodyPart.RIGHT_HIP
import com.ijonsabae.presentation.shot.ai.data.BodyPart.RIGHT_SHOULDER
import com.ijonsabae.presentation.shot.ai.ml.TimestampedData
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt


object PostureExtractor {
    var minFinishGap = 100f
    var minFollowThroughGap = 100f
    var minImpactGap = 100f
    var minDownSwingGap = 100f
    var minTopOfSwingGap = 100f
    var minMidBackSwingGap = 100f
    var minTakeAwayGap = 100f
    var minAddressGap = 100f
    var manualPoseIndexArray = Array(8, {0})

    var backswingStartTime : Long = 0
    var backswingEndTime : Long = 0
    var downswingStartTime : Long = 0
    var downswingEndTime : Long = 0
    var resultSkipMotionStartTime : Long = 0
    var isDownSwingEnd = false

    lateinit var imageDataList: List<TimestampedData<Bitmap>>

    fun initVariables(imageList:List<TimestampedData<Bitmap>>) {
        minFinishGap = 100f
        minFollowThroughGap = 100f
        minImpactGap = 100f
        minDownSwingGap = 100f
        minTopOfSwingGap = 100f
        minMidBackSwingGap = 100f
        minTakeAwayGap = 100f
        minAddressGap = 100f
        manualPoseIndexArray = Array(8) { 0 }

        backswingStartTime = 0
        backswingEndTime = 0
        downswingStartTime = 0
        downswingEndTime = 0
        resultSkipMotionStartTime = 0
        isDownSwingEnd = false

        imageDataList = imageList
    }


    fun checkResultSkipMotion(jointData: List<KeyPoint>): Boolean {
        // 1. 카메라 안에 있는지 판별
        if ((jointData[NOSE.position].score) < 0.3 ||
            (jointData[LEFT_ANKLE.position].score) < 0.3 ||
            (jointData[RIGHT_ANKLE.position].score < 0.3)
        ) {
            return false
        }

        val leftWristX = jointData[LEFT_WRIST.position].coordinate.x
        val leftWristY = jointData[LEFT_WRIST.position].coordinate.y
        val leftElbowX = jointData[LEFT_ELBOW.position].coordinate.x
        val leftElbowY = jointData[LEFT_ELBOW.position].coordinate.y
        val noseX = jointData[NOSE.position].coordinate.x

        val deltaX = leftWristX - leftElbowX
        val deltaY = leftElbowY - leftWristY

        val angleRadians = atan2(deltaY, deltaX)
        val angleDegrees = Math.toDegrees(angleRadians.toDouble())

        val currentTime = System.currentTimeMillis()
        Log.d("processDetectedInfo", "RESULT 상태일 때 내부 함수, chckskipmotion 함수 내부 접근")
        if (leftWristX > leftElbowX && leftElbowX > noseX) {
            val wristElbowDegreeGap = abs(0.0 - angleDegrees).toFloat()
            if (wristElbowDegreeGap < 20.0) {
                resultSkipMotionStartTime = currentTime
                Log.d("processDetectedInfo", "왼쪽 팔뻗음 인식")
            }
        } else if (leftWristX < leftElbowX && leftWristX <= noseX) {
            val wristElbowDegreeGap = abs(180.0 - angleDegrees).toFloat()
            if (wristElbowDegreeGap < 20.0) {
                Log.d("processDetectedInfo", "오른쪽 팔뻗음 인식")
                if (currentTime - resultSkipMotionStartTime < 1000) {
                    return true
                }
            }
        }
        return false
    }


    fun checkFinish(index: Int, jointData: List<KeyPoint>) {
        //오른쪽 어깨.x가 왼쪽 골반.x보다 왼쪽에 있고 왼손목의 x좌표가 가장 작을때
        val leftWristX = jointData[LEFT_WRIST.position].coordinate.x
        val rightShoulderX = jointData[RIGHT_SHOULDER.position].coordinate.x
        val leftHipX = jointData[LEFT_HIP.position].coordinate.x

        if (rightShoulderX >= leftHipX) {
            if (minFinishGap > leftWristX) {
                minFinishGap = leftWristX
                manualPoseIndexArray[7] = index
            }
        }
    }

    fun checkFollowThrough(index: Int, jointData: List<KeyPoint>) {
        // 왼손.x가 왼어꺠.x보다 클 때
        val leftWristX = jointData[LEFT_WRIST.position].coordinate.x
        val leftWristY = jointData[LEFT_WRIST.position].coordinate.y
        val rightHipY = jointData[RIGHT_HIP.position].coordinate.y
        val leftShoulderX = jointData[LEFT_SHOULDER.position].coordinate.x

        if (leftShoulderX < leftWristX) {
            val followThroughGap = abs(leftWristY - rightHipY)
            if (minFollowThroughGap > followThroughGap) {
                minFollowThroughGap = followThroughGap
                manualPoseIndexArray[6] = index
            }
        }
    }

    fun checkImpact(index: Int, jointData: List<KeyPoint>) {
        // 임팩트 - 손목의 평균 좌표가 골반의 중앙과 가장 가까울 때
        val leftHipX = jointData[LEFT_HIP.position].coordinate.x
        val rightHipX = jointData[RIGHT_HIP.position].coordinate.x
        val leftWristX = jointData[LEFT_WRIST.position].coordinate.x
        val leftWristY = jointData[LEFT_WRIST.position].coordinate.y
        val leftElbowX = jointData[LEFT_ELBOW.position].coordinate.x
        val leftElbowY = jointData[LEFT_ELBOW.position].coordinate.y

        if (leftWristY > leftElbowY && leftWristX < leftElbowX && leftWristX >= rightHipX) {
            //손목이 골반 아래 위치할 때 골반 중심과 x좌표 거리가 가장 가까운 경우를 추출

            val hipCenterX = (rightHipX + leftHipX) / 2
            val impactGap = abs(hipCenterX - leftWristX)

            if (minImpactGap > impactGap) {
                minImpactGap = impactGap
                manualPoseIndexArray[5] = index
                downswingEndTime = imageDataList[index].timestamp
            }
        }
    }

    fun checkDownSwing(index: Int, jointData: List<KeyPoint>) {
        // 다운스윙 - 왼손이 가장 왼쪽에 있고 허리와 어꺠 사이에 있을때
        val leftWristX = jointData[LEFT_WRIST.position].coordinate.x
        val leftWristY = jointData[LEFT_WRIST.position].coordinate.y
        val rightHipX = jointData[RIGHT_HIP.position].coordinate.x
        val rightShoulderY = jointData[RIGHT_SHOULDER.position].coordinate.y
        val rightHipY = jointData[RIGHT_HIP.position].coordinate.y

        if (
            leftWristX < rightHipX &&
            rightShoulderY < leftWristY &&
            leftWristY < rightHipY
        ) {
            if (minDownSwingGap > leftWristX) {
                minDownSwingGap = leftWristX
                manualPoseIndexArray[4] = index
            }
        }
    }

    fun checkTopOfSwing(index: Int, jointData: List<KeyPoint>) {
        // 왼 손목이 어깨보다 높을 때 and
        // 왼손목과 코의 x 좌표가 가장 가까울 때

        val leftWristX = jointData[LEFT_WRIST.position].coordinate.x
        val leftWristY = jointData[LEFT_WRIST.position].coordinate.y
        val leftShoulderY = jointData[LEFT_SHOULDER.position].coordinate.y
        val noseX = jointData[NOSE.position].coordinate.x
        val noseY = jointData[NOSE.position].coordinate.y

        if (leftWristY < leftShoulderY && leftWristX < noseX) {
            val noseWristGap = noseX - leftWristX
            if (minTopOfSwingGap > noseWristGap) {
                minTopOfSwingGap = noseWristGap
                manualPoseIndexArray[3] = index
            }
        }

        // 코보다 왼손 높이가 커지는 시점을 갱신
        if (leftWristX < noseY) {
            if (!isDownSwingEnd && leftWristY < noseY && leftWristX < noseX) {
                downswingStartTime = imageDataList[index + 1].timestamp
                isDownSwingEnd = true
            }
            if (isDownSwingEnd && leftWristY >= noseY && leftWristX < noseX) {
                backswingEndTime = imageDataList[index - 2].timestamp
                isDownSwingEnd = false
            }
        }
        // 코보다 왼손이 더 높아지는 경우가 없을 경우 왼쪽 어깨를 기준으로 판단
        else {
            if (!isDownSwingEnd && leftWristY < leftShoulderY && leftWristX < noseX) {
                downswingStartTime = imageDataList[index + 1].timestamp
                isDownSwingEnd = true
            }
            if (isDownSwingEnd && leftWristY >= leftShoulderY && leftWristX < noseX) {
                backswingEndTime = imageDataList[index - 2].timestamp
                isDownSwingEnd = false
            }
        }
    }


    fun checkMidBackSwing(index: Int, jointData: List<KeyPoint>) {

        val leftWristX = jointData[LEFT_WRIST.position].coordinate.x
        val leftWristY = jointData[LEFT_WRIST.position].coordinate.y
        val leftElbowX = jointData[LEFT_ELBOW.position].coordinate.x
        val leftElbowY = jointData[LEFT_ELBOW.position].coordinate.y
        val leftShoulderY = jointData[LEFT_SHOULDER.position].coordinate.y
        val leftHipY = jointData[LEFT_HIP.position].coordinate.y

        //손목이 어꺠보다 낮고 골반보다 높을 때 왼팔 손목과 팔꿈치가 +- 10도 내외 일 때 왼손목 x 좌표가 가장 0에 가까운 경우
        if (leftWristY in leftShoulderY..leftHipY) {
            //왼 손목을 중심으로 왼 팔꿈치까지의 각도를 계산
            val deltaX = leftElbowX - leftWristX
            val deltaY = leftWristY - leftElbowY

            //라디안 값 반환
            val angleRadians = atan2(deltaY, deltaX)

            //라디안을 degree 단위로 변환
            val angleDegrees = Math.toDegrees(angleRadians.toDouble())

            if (angleDegrees in -10.0..10.0) {
                if (minMidBackSwingGap > leftWristX) {
                    minMidBackSwingGap = leftWristX
                    manualPoseIndexArray[2] = index
                }
            }
        }
    }

    fun checkTakeAway(index: Int, jointData: List<KeyPoint>) {
        //왼손목이 왼 팔꿈치보다 왼쪽아래 있을 때 왼쪽손목기준 팔꿈치가 45도와 가장 가까운 경우를 선택
        val leftWristX = jointData[LEFT_WRIST.position].coordinate.x
        val leftWristY = jointData[LEFT_WRIST.position].coordinate.y
        val leftElbowX = jointData[LEFT_ELBOW.position].coordinate.x
        val leftElbowY = jointData[LEFT_ELBOW.position].coordinate.y

        if (leftWristX < leftElbowX && leftWristY > leftElbowY) {
            val deltaX = leftElbowX - leftWristX
            val deltaY = leftWristY - leftElbowY

            val angleRadians = atan2(deltaY, deltaX)

            val angleDegrees = Math.toDegrees(angleRadians.toDouble())
            val wristElbowDegreeGap = abs(45.0 - angleDegrees).toFloat()
            if (minTakeAwayGap > wristElbowDegreeGap) {
                minTakeAwayGap = wristElbowDegreeGap
                manualPoseIndexArray[1] = index
            }
        }
    }

    fun checkAddress(index: Int, jointData: List<KeyPoint>) {
        // 골반과 거리가 가장 가까운 시점을 검사
        val rightHipX = jointData[RIGHT_HIP.position].coordinate.x
        val rightHipY = jointData[RIGHT_HIP.position].coordinate.y
        val leftElbowY = jointData[LEFT_ELBOW.position].coordinate.y

        val leftWristX = jointData[LEFT_WRIST.position].coordinate.x
        val leftWristY = jointData[LEFT_WRIST.position].coordinate.y

        if (leftWristY > leftElbowY && leftWristX > rightHipX) {
            // 거리 계산을 위해 제곱근을 사용
            val hipWristDistance = sqrt(
                (rightHipX - leftWristX).pow(2) +
                        (rightHipY - leftWristY).pow(2)
            )

            // minAddressGap이 거리보다 큰 경우에만 업데이트
            if (minAddressGap > hipWristDistance) {
                minAddressGap = hipWristDistance
                if (index + 5 <= imageDataList.size - 1) {
                    backswingStartTime = imageDataList[index + 5].timestamp // 스윙 시작시간 갱신
                    manualPoseIndexArray[0] = index + 5
                } else {
                    backswingStartTime = imageDataList[index].timestamp // 스윙 시작시간 갱신
                    manualPoseIndexArray[0] = index
                }
            }
        }
    }
}