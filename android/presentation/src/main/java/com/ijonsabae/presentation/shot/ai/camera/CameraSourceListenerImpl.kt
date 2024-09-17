package com.ijonsabae.presentation.shot.ai.camera

import MoveNet
import android.util.Log
import com.ijonsabae.presentation.shot.CameraState.ADDRESS
import com.ijonsabae.presentation.shot.CameraState.ANALYZING
import com.ijonsabae.presentation.shot.CameraState.POSITIONING
import com.ijonsabae.presentation.shot.CameraState.SWING
import com.ijonsabae.presentation.shot.SwingViewModel
import com.ijonsabae.presentation.shot.ai.data.BodyPart.LEFT_ANKLE
import com.ijonsabae.presentation.shot.ai.data.BodyPart.LEFT_ELBOW
import com.ijonsabae.presentation.shot.ai.data.BodyPart.LEFT_SHOULDER
import com.ijonsabae.presentation.shot.ai.data.BodyPart.LEFT_WRIST
import com.ijonsabae.presentation.shot.ai.data.BodyPart.NOSE
import com.ijonsabae.presentation.shot.ai.data.BodyPart.RIGHT_ANKLE
import com.ijonsabae.presentation.shot.ai.data.BodyPart.RIGHT_ELBOW
import com.ijonsabae.presentation.shot.ai.data.BodyPart.RIGHT_SHOULDER
import com.ijonsabae.presentation.shot.ai.data.BodyPart.RIGHT_WRIST
import com.ijonsabae.presentation.shot.ai.data.Person
import com.ijonsabae.presentation.shot.ai.ml.PoseClassifier

class CameraSourceListenerImpl(
    private val swingViewModel: SwingViewModel,
    private val poseDetector: MoveNet?,
    private val classifier4: PoseClassifier?,
    private val classifier8: PoseClassifier?
) : CameraSourceListener {

    // 이전 실행 시간으로부터 최소 1초 지나야 실행
    private var lastExecutionTime = 0L

    override fun onDetectedInfo(person: Person) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastExecutionTime >= 1000) {
            lastExecutionTime = currentTime
            processDetectedInfo(person)
        }
    }

    private var lastLogTime = 0L

    private fun logWithThrottle(message: String) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastLogTime >= 1000) { // 1초 이상 지났는지 확인
            Log.d("싸피", message)
            lastLogTime = currentTime
        }
    }

    /**
     * TODO: 좌타일 경우 좌우 관절을 뒤집어야 합니다. 얼굴은 빼구요
     * estimatePoses()에서 뒤집으면 그릴 때도 반대로 그려줘서 추론때만 반대로 써서 판단해야 해요
     **/
    private fun processDetectedInfo(person: Person) {
        val point = person.keyPoints

        // 5. 스윙의 마지막 동작 체크 (손목 y변화 감지해서 변곡점이 스윙 마무리라고 판단)
        if (swingViewModel.currentState.value == SWING &&
            point[LEFT_WRIST.position].coordinate.x < point[LEFT_SHOULDER.position].coordinate.x &&
            point[LEFT_WRIST.position].coordinate.x < point[LEFT_SHOULDER.position].coordinate.x &&
            isIncreasing().not()
        ) {
            swingViewModel.setCurrentState(ANALYZING)
            val swingData = extractSwing()

            if (swingData != null) {
                // TODO: 8개 프레임 분석하기

                // TODO: 템포, 백스윙, 다운스윙 시간 분석하기

                // TODO: 영상 메모리에 올리기

                // TODO: 영상 서버에 저장하기 (비동기)

                // TODO: 스윙 분석 결과 표시 (일정시간)

            } else {
                // TODO: 다시 스윙해주세요 표시 (일정시간)

            }
        }

        // 4. 스윙하는 동안은 안내 메세지 안변하도록 유지
        if (swingViewModel.currentState.value == SWING &&
            ((point[LEFT_ELBOW.position].coordinate.x > point[LEFT_SHOULDER.position].coordinate.x) ||
                    (point[RIGHT_ELBOW.position].coordinate.x < point[RIGHT_SHOULDER.position].coordinate.x))
        ) return

        // 1. 몸 전체가 카메라 화면에 들어오는지 체크
        if ((point[NOSE.position].score) < 0.3 ||
            (point[LEFT_ANKLE.position].score) < 0.3 ||
            (point[RIGHT_ANKLE.position].score < 0.3)
        ) {
            swingViewModel.setCurrentState(POSITIONING)
        }
        // 2. 어드레스 자세 체크
        else if ((point[LEFT_WRIST.position].coordinate.y > point[LEFT_ELBOW.position].coordinate.y &&
                    point[RIGHT_WRIST.position].coordinate.y > point[RIGHT_ELBOW.position].coordinate.y &&
                    point[LEFT_WRIST.position].coordinate.x >= point[LEFT_SHOULDER.position].coordinate.x &&
                    point[LEFT_WRIST.position].coordinate.x <= point[RIGHT_SHOULDER.position].coordinate.x &&
                    point[RIGHT_WRIST.position].coordinate.x <= point[RIGHT_SHOULDER.position].coordinate.x &&
                    point[RIGHT_WRIST.position].coordinate.x >= point[LEFT_SHOULDER.position].coordinate.x).not()
        ) {
            swingViewModel.setCurrentState(ADDRESS)
        }
        // 3. 스윙해주세요!
        else {
            swingViewModel.setCurrentState(SWING)
        }
    }

    /**
     * (스윙 영상 + 영상에 그려줄 관절 좌표)와 8동작의 프레임을 반환
     */
    private fun extractSwing(): Unit? {
        val queuedData = poseDetector?.getQueuedData()

        // 큐에는 최대 72개의 데이터가 들어있음 -> 최대 24fps 제한이므로 3초 이상의 데이터임
        val imagesWithTimestampList = queuedData?.first?.reversed()
        val jointDataList = queuedData?.second?.reversed()

        if (imagesWithTimestampList == null || jointDataList == null) {
            return null
        }

        // 1. 대표적인 8개의 프레임 뽑기
        for (joint in jointDataList) {
//            classifier4.classify()
        }

        // TODO: 8개 프레임이 안뽑힐 경우 null 반환

        // 2. 영상 만들기

        return null
    }

    // 손목의 y축 좌표가 상승하는 추세인지 보는 함수
    private fun isIncreasing(): Boolean {
        poseDetector?.let { detector ->
            val (_, jointData) = detector.getQueuedData()

            // 데이터가 20개 미만이면 추세를 판단할 수 없음
            if (jointData.size < 20) {
                return false
            }

            // 최대 20개의 최근 데이터 사용 <- 여기서 보는 데이터의 개수가 스윙 영상이 피니쉬의 어디까지 녹화될지 영향을 줌
            val recentJointData = jointData.takeLast(20.coerceAtMost(jointData.size))

            // 각 프레임에서 오른쪽 손목의 y 좌표를 추출
            val wristYCoordinates = recentJointData.mapNotNull { frame ->
                frame.find { it.bodyPart == RIGHT_WRIST }?.coordinate?.y
            }

            // y 좌표가 전반적으로 감소하는지 확인 (화면 상단으로 갈수록 y 값이 작아짐)
            for (i in 1 until wristYCoordinates.size) {
                if (wristYCoordinates[i] >= wristYCoordinates[i - 1]) {
                    return false // 증가하거나 같은 경우가 있으면 상승 추세가 아님
                }
            }
            return true // 모든 비교에서 감소했다면 상승 추세임
        }
        return false // poseDetector가 null인 경우
    }
}