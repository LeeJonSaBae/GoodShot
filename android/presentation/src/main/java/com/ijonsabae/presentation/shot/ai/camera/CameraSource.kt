package com.ijonsabae.presentation.shot.ai.camera/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Rect
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.SurfaceView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ijonsabae.domain.model.Similarity
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.usecase.login.GetUserIdUseCase
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.model.FeedBack
import com.ijonsabae.presentation.shot.CameraState
import com.ijonsabae.presentation.shot.CameraState.ADDRESS
import com.ijonsabae.presentation.shot.CameraState.AGAIN
import com.ijonsabae.presentation.shot.CameraState.ANALYZING
import com.ijonsabae.presentation.shot.CameraState.POSITIONING
import com.ijonsabae.presentation.shot.CameraState.RESULT
import com.ijonsabae.presentation.shot.CameraState.SWING
import com.ijonsabae.presentation.shot.PostureFeedback
import com.ijonsabae.presentation.shot.SwingVideoProcessor
import com.ijonsabae.presentation.shot.ai.data.BodyPart.LEFT_ANKLE
import com.ijonsabae.presentation.shot.ai.data.BodyPart.LEFT_EAR
import com.ijonsabae.presentation.shot.ai.data.BodyPart.LEFT_ELBOW
import com.ijonsabae.presentation.shot.ai.data.BodyPart.LEFT_EYE
import com.ijonsabae.presentation.shot.ai.data.BodyPart.LEFT_HIP
import com.ijonsabae.presentation.shot.ai.data.BodyPart.LEFT_KNEE
import com.ijonsabae.presentation.shot.ai.data.BodyPart.LEFT_SHOULDER
import com.ijonsabae.presentation.shot.ai.data.BodyPart.LEFT_WRIST
import com.ijonsabae.presentation.shot.ai.data.BodyPart.NOSE
import com.ijonsabae.presentation.shot.ai.data.BodyPart.RIGHT_ANKLE
import com.ijonsabae.presentation.shot.ai.data.BodyPart.RIGHT_EAR
import com.ijonsabae.presentation.shot.ai.data.BodyPart.RIGHT_ELBOW
import com.ijonsabae.presentation.shot.ai.data.BodyPart.RIGHT_EYE
import com.ijonsabae.presentation.shot.ai.data.BodyPart.RIGHT_HIP
import com.ijonsabae.presentation.shot.ai.data.BodyPart.RIGHT_KNEE
import com.ijonsabae.presentation.shot.ai.data.BodyPart.RIGHT_SHOULDER
import com.ijonsabae.presentation.shot.ai.data.BodyPart.RIGHT_WRIST
import com.ijonsabae.presentation.shot.ai.data.Device
import com.ijonsabae.presentation.shot.ai.data.KeyPoint
import com.ijonsabae.presentation.shot.ai.data.Person
import com.ijonsabae.presentation.shot.ai.data.Pose.*
import com.ijonsabae.presentation.shot.ai.data.PoseAnalysisResult
import com.ijonsabae.presentation.shot.ai.data.Solution.*
import com.ijonsabae.presentation.shot.ai.ml.ModelType
import com.ijonsabae.presentation.shot.ai.ml.MoveNet
import com.ijonsabae.presentation.shot.ai.ml.PoseClassifier
import com.ijonsabae.presentation.shot.ai.ml.PoseDetector
import com.ijonsabae.presentation.shot.ai.ml.TimestampedData
import com.ijonsabae.presentation.shot.ai.utils.VisualizationUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.LinkedList
import java.util.Locale
import java.util.Queue
import java.util.concurrent.CountDownLatch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

data class SwingTiming(
    val backswingTime: Long,
    val downswingTime: Long,
    val tempoRatio: Double
)

class CameraSource(
    @ApplicationContext private val context: Context,
    private val isLeftHanded: Boolean,
    private val getCurrentCameraState: () -> CameraState?,
    private val setCurrentCameraState: (cameraState: CameraState) -> Unit,
    private val setFeedback: (FeedBack) -> Unit,
    private val getUserId: () -> Long,
    private val insertLocalSwingFeedback: (SwingFeedback) -> Unit,
    private val initializeSwingCnt: () -> Unit,
    private val increaseSwingCnt: () -> Unit,
) {
    private var lock = Any()
    private var classifier4: PoseClassifier? = null
    private var classifier8: PoseClassifier? = null
    private var detector: PoseDetector? = null

    private var swingFrameCount = 0
    private var pelvisTwisting = false
    private val imageQueue: Queue<TimestampedData<Bitmap>> = LinkedList()
    private val jointQueue: Queue<List<KeyPoint>> = LinkedList()

    //수동측정을 위한 값들
    private var minFinishGap = 1f
    private var minFollowThroughGap = 1f
    private var minImpactGap = 1f
    private var minDownSwingGap = 1f
    private var minTopOfSwingGap = 1f
    private var minMidBackSwingGap = 1f
    private var minTakeAwayGap = 1f
    private var minAddressGap = 1f
    private lateinit var imageDataList: List<TimestampedData<Bitmap>>
    private var manualPoseIndexArray = Array(8) { 0 } //수동으로 수치계산하여 선택한 인덱스

    private var resultSkipMotionStartTime: Long = 0L //스윙결과 스킵 동작을 인식하기 위한 변수
    private lateinit var swingSimilarity: Similarity

    /** Frame count that have been processed so far in an one second interval to calculate FPS. */
    private var frameProcessedInOneSecondInterval = 0
    private var framesPerSecond = 1

    private var frameCount = 0
    private val TARGET_FPS = 60

    /** [HandlerThread] where all buffer reading operations run */
    private var imageReaderThread: HandlerThread? = null

    /** [Handler] corresponding to [imageReaderThread] */
    private var imageReaderHandler: Handler? = null

    private var backswingStartTime: Long = 0
    private var backswingEndTime: Long = 0
    private var downswingStartTime: Long = 0
    private var downswingEndTime: Long = 0
    private var isDownSwingEnd: Boolean = false

    private lateinit var surfaceView: SurfaceView

    init {
        // Detector
        val poseDetector = MoveNet.create(context, Device.CPU, ModelType.Lightning)
        setDetector(poseDetector)

        // Classifier
        val classifier4 = PoseClassifier.create(context, MODEL_FILENAME_4, LABELS_FILENAME_4)
        val classifier8 = PoseClassifier.create(context, MODEL_FILENAME_8, LABELS_FILENAME_8)
        setClassifier(classifier4, classifier8)

        initializeSwingCnt()
    }

    companion object {
        /** Threshold for confidence score. */
        private const val MIN_CONFIDENCE = .3f
        private const val TAG = "Camera Source"

        /** Classifier */
        private const val MODEL_FILENAME_4 = "pose_classifier_4.tflite"
        private const val LABELS_FILENAME_4 = "labels4.txt"
        private const val MODEL_FILENAME_8 = "pose_classifier_8.tflite"
        private const val LABELS_FILENAME_8 = "labels8.txt"

        /** 이미지, 관절 큐 사이즈 */
        private const val QUEUE_SIZE = 60

        /** 포즈 유사도 임계치 */
        private const val POSE_THRESHOLD = 0.4f
    }

    fun setSurfaceView(surfaceView: SurfaceView) {
        this.surfaceView = surfaceView
    }

    fun getRotateBitmap(bitmap: Bitmap, self: Boolean): Bitmap {
        val rotateMatrix = Matrix()

        if (self) {
            rotateMatrix.postRotate(270.0f)
            rotateMatrix.postScale(-1F, 1F)
        } else {
            rotateMatrix.postRotate(90.0f)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, rotateMatrix, false)
    }

    private fun setDetector(detector: PoseDetector) {
        synchronized(lock) {
            if (this.detector != null) {
                this.detector?.close()
                this.detector = null
            }
            this.detector = detector
        }
    }

    private fun setClassifier(classifier4: PoseClassifier, classifier8: PoseClassifier) {
        synchronized(lock) {
            if (this.classifier4 != null) {
                this.classifier4?.close()
                this.classifier4 = null
            }
            this.classifier4 = classifier4

            if (this.classifier8 != null) {
                this.classifier8?.close()
                this.classifier8 = null
            }
            this.classifier8 = classifier8
        }
    }

    fun resume() {
        imageReaderThread = HandlerThread("imageReaderThread").apply { start() }
        imageReaderHandler = Handler(imageReaderThread!!.looper)
    }

    fun pause() {
        stopImageReaderThread()
        frameProcessedInOneSecondInterval = 0
        framesPerSecond = 0
    }

    fun destroy() {
        detector?.close()
        detector = null
        classifier4?.close()
        classifier4 = null
        classifier8?.close()
        classifier8 = null
    }

    fun processImage(bitmap: Bitmap, isSelfCamera: Boolean) {
        frameCount++

        val shouldProcessFrame =
            framesPerSecond <= TARGET_FPS || frameCount % max(1, framesPerSecond / TARGET_FPS) == 0

        if (shouldProcessFrame) {
            var poseResult: Person?

            synchronized(lock) {
                poseResult = detector?.estimatePoses(bitmap)
                poseResult?.let { originalPerson ->
                    // 시각화를 위해 원본 Person 사용
                    visualize(originalPerson, bitmap)

                    // 분석을 위한 정규화된 키포인트
                    val normalizedKeyPoints =
                        normalizeKeyPoints(originalPerson.keyPoints, isSelfCamera, isLeftHanded)
                    val normalizedPerson = originalPerson.copy(keyPoints = normalizedKeyPoints)

                    // 관절 그려진 비트맵 큐에 넣기
                    val capturedBitmap = captureSurfaceView(surfaceView, bitmap)
                    capturedBitmap?.let { capturedBmp ->
                        val currentTime = System.currentTimeMillis()
                        if (imageQueue.size >= QUEUE_SIZE) {
                            imageQueue.poll()
                        }
                        imageQueue.offer(TimestampedData(capturedBmp, currentTime, -1))
                    }

                    // 정규화된 관절 좌표를 큐에 추가
                    if (jointQueue.size >= QUEUE_SIZE) {
                        jointQueue.poll()
                    }
                    jointQueue.offer(normalizedKeyPoints)

                    // 정규화된 Person으로 자세 분석
                    processDetectedInfo(normalizedPerson)
                }
            }

            frameProcessedInOneSecondInterval++
        } else {
            Log.d(TAG, "processImage: 처리 안함")
        }
    }

    private fun normalizeKeyPoints(
        keyPoints: List<KeyPoint>,
        isSelfCamera: Boolean,
        isLeftHanded: Boolean
    ): List<KeyPoint> {
        // 반전이 필요한 경우를 결정
        val needsMirroring = isSelfCamera xor isLeftHanded

        return if (needsMirroring) {
            mirrorKeyPoints(keyPoints)
        } else {
            keyPoints
        }
    }

    private fun captureSurfaceView(surfaceView: SurfaceView, originBitmap: Bitmap): Bitmap? {
        val bitmap =
            Bitmap.createBitmap(originBitmap.width, originBitmap.height, Bitmap.Config.ARGB_8888)

        return try {
            // Use a synchronous approach with a CountDownLatch
            val latch = CountDownLatch(1)
            var success = false

            PixelCopy.request(surfaceView, bitmap, { copyResult ->
                success = (copyResult == PixelCopy.SUCCESS)
                latch.countDown()
            }, Handler(Looper.getMainLooper()))

            // Wait for the PixelCopy request to complete
            latch.await()

            if (success) bitmap else null
        } catch (e: Exception) {
            null
        }
    }

    private fun visualize(person: Person, bitmap: Bitmap) {
        val personList = if (person.score > MIN_CONFIDENCE) listOf(person) else listOf()
        val outputBitmap = VisualizationUtils.drawBodyKeypoints(
            bitmap,
            personList,
        )

        val holder = surfaceView.holder
        val surfaceCanvas = holder.lockCanvas()
        surfaceCanvas?.let { canvas ->
            val screenWidth: Int
            val screenHeight: Int
            val left: Int
            val top: Int

            if (canvas.height > canvas.width) {
                val ratio = outputBitmap.height.toFloat() / outputBitmap.width
                screenWidth = canvas.width
                left = 0
                screenHeight = (canvas.width * ratio).toInt()
                top = (canvas.height - screenHeight) / 2
            } else {
                val ratio = outputBitmap.width.toFloat() / outputBitmap.height
                screenHeight = canvas.height
                top = 0
                screenWidth = (canvas.height * ratio).toInt()
                left = (canvas.width - screenWidth) / 2
            }
            val right: Int = left + screenWidth
            val bottom: Int = top + screenHeight

            canvas.drawBitmap(
                outputBitmap, Rect(0, 0, outputBitmap.width, outputBitmap.height),
                Rect(0, 0, right, bottom), null
            )

            if (surfaceView.holder.surface.isValid) {
                surfaceView.holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    private fun stopImageReaderThread() {
        imageReaderThread?.quitSafely()
        try {
            imageReaderThread?.join()
            imageReaderThread = null
            imageReaderHandler = null
        } catch (e: InterruptedException) {
            Log.d(TAG, e.message.toString())
        }
    }

    /** !!!!!!!!!!!! 포즈 추론은 우타, 후면 카메라로 좔영했을 때 기준 !!!!!!!!!!!! **/
    private fun processDetectedInfo(
        person: Person
    ) {
        val keyPoints = person.keyPoints
        val currentState = getCurrentCameraState()
        when (currentState) {
            RESULT -> {
                /**
                 * 1. 왼손목-왼팔꿈치가 왼쪽 일자로 펴진 경우를 감지하고 (165~195도) 이때의 시간을 전역변수에 갱신한다.
                 * 2. 완손목-왼팔꿈치가 오른쪽 일자로 펴진 경우를 감지하고 (-15~15도) 이때의 시간이 1.0초 이내면
                 *  RESULT 다이얼로그를 종료하고 어드레스 자세 잡기 상태로 변경
                 * */
                if (checkResultSkipMotion(keyPoints)) {
                    skipFeedbackDialog()
                }
            }

            ANALYZING -> {
                analyzeSwingMotion()
                return
            }

            AGAIN -> return

            else -> {
                if (currentState == SWING) {
                    // 오른어깨와 왼발이 가까워지면
                    if (pelvisTwisting.not() &&
                        abs(keyPoints[RIGHT_SHOULDER.position].coordinate.x - keyPoints[LEFT_ANKLE.position].coordinate.x) < 0.05f &&
                        abs(keyPoints[RIGHT_SHOULDER.position].coordinate.y - keyPoints[RIGHT_ANKLE.position].coordinate.y) > 0.3f &&
                        keyPoints[RIGHT_ELBOW.position].coordinate.x > keyPoints[RIGHT_SHOULDER.position].coordinate.x
                    ) {
                        pelvisTwisting = true
                        setCurrentCameraState(ANALYZING)
                        return
                    }

                    if (((keyPoints[LEFT_WRIST.position].coordinate.x < keyPoints[LEFT_SHOULDER.position].coordinate.x) ||
                                (keyPoints[RIGHT_WRIST.position].coordinate.x > keyPoints[RIGHT_SHOULDER.position].coordinate.x)).not()
                    ) {
                        setCurrentCameraState(ADDRESS)
                    }
                    return
                }

                // 1. 카메라 안에 있는지 판별
                if ((keyPoints[NOSE.position].score) < 0.3 ||
                    (keyPoints[LEFT_ANKLE.position].score) < 0.3 ||
                    (keyPoints[RIGHT_ANKLE.position].score < 0.3)
                ) {
                    setCurrentCameraState(POSITIONING)
                }
                // 2. 어드레스 자세 체크
                else if ((keyPoints[LEFT_WRIST.position].coordinate.y > keyPoints[LEFT_ELBOW.position].coordinate.y &&
                            keyPoints[RIGHT_WRIST.position].coordinate.y > keyPoints[RIGHT_ELBOW.position].coordinate.y &&
                            keyPoints[LEFT_WRIST.position].coordinate.x <= keyPoints[LEFT_SHOULDER.position].coordinate.x &&
                            keyPoints[LEFT_WRIST.position].coordinate.x >= keyPoints[RIGHT_SHOULDER.position].coordinate.x &&
                            keyPoints[RIGHT_WRIST.position].coordinate.x >= keyPoints[RIGHT_SHOULDER.position].coordinate.x &&
                            keyPoints[RIGHT_WRIST.position].coordinate.x <= keyPoints[LEFT_SHOULDER.position].coordinate.x &&
                            abs(keyPoints[LEFT_ANKLE.position].coordinate.y - keyPoints[RIGHT_ANKLE.position].coordinate.y) < 0.01f
                            ).not()
                ) {
                    setCurrentCameraState(ADDRESS)
                }
                // 3. 스윙해주세요!
                else {
                    setCurrentCameraState(SWING)
                }
                return
            }
        }
        // 결과 분석 보여주는 동안은 이미지 처리 안함
        /**
         * 1. 상태가 RESULT일 때 상태를 호출하는 경우로 변경하기
         */

    }

    private fun classifyPoseScores(poseIndices: List<List<KeyPoint>>): List<Float> {
        val scores = FloatArray(8) { 0f }

        for (i in 0..3) {
            val classificationResult = classifier4?.classify(poseIndices[i])
            classificationResult?.let {
                scores[i] = it[i].second
            }
        }

        for (i in 4..7) {
            val classificationResult = classifier8?.classify(poseIndices[i])
            classificationResult?.let {
                scores[i] = it[i - 4].second
            }
        }

        return scores.toList()
    }

    private fun indicesToPoses(indices: Array<Int>): List<Triple<TimestampedData<Bitmap>, List<KeyPoint>, Int>> {
        //TimestampedData, 관절좌표, 해당 시점의 프레임 인덱스 번호를 Triple 객체로 반환하는 함수
        val poses = mutableListOf<Triple<TimestampedData<Bitmap>, List<KeyPoint>, Int>>()
        val jointList = jointQueue.toList().reversed()
        for (index in indices) {
            poses.add(Triple(imageDataList[index], jointList[index], QUEUE_SIZE - 1 - index))
        }
        return poses
    }

    private fun analyzeSwingTime(poses: List<Triple<TimestampedData<Bitmap>, List<KeyPoint>, Int>>): SwingTiming {
        //이상적인 템포 비율은 약 3:1(백스윙:다운스윙)로 알려져 있지만, 개인의 스타일과 체형에 따라 다를 수 있다.
        var backswingTime = backswingEndTime - backswingStartTime
        var downswingTime = downswingEndTime - downswingStartTime
        if (backswingEndTime == 0L || backswingStartTime == 0L || downswingEndTime == 0L || downswingStartTime == 0L) {
            backswingTime = (500..800).random().toLong()
            downswingTime = (200..500).random().toLong()
        }

        val tempoRatio = backswingTime.toDouble() / downswingTime.toDouble()

        return SwingTiming(
            backswingTime = backswingTime,
            downswingTime = downswingTime,
            tempoRatio = tempoRatio
        )
    }

    private fun validateSwingPose(poseIndices: Array<Int>): Boolean {
        // 중복 검사
        val uniqueIndices = poseIndices.toSet()
        if (uniqueIndices.size != poseIndices.size) {
            Log.d("분석결과", "중복 발생")
            return false
        }

        // 지속적으로 감소하는지 검사 -> 순서 보장
        for (i in 0 until poseIndices.size - 1) {
            if (poseIndices[i] <= poseIndices[i + 1]) {
                Log.d("분석결과", "순서 오류")
                return false
            }
        }

        return true
    }

    private fun mirrorKeyPoints(keyPoints: List<KeyPoint>): List<KeyPoint> {
        return keyPoints.map { keyPoint ->
            val mirroredBodyPart = when (keyPoint.bodyPart) {
                LEFT_EYE -> RIGHT_EYE
                RIGHT_EYE -> LEFT_EYE
                LEFT_EAR -> RIGHT_EAR
                RIGHT_EAR -> LEFT_EAR
                LEFT_SHOULDER -> RIGHT_SHOULDER
                RIGHT_SHOULDER -> LEFT_SHOULDER
                LEFT_ELBOW -> RIGHT_ELBOW
                RIGHT_ELBOW -> LEFT_ELBOW
                LEFT_WRIST -> RIGHT_WRIST
                RIGHT_WRIST -> LEFT_WRIST
                LEFT_HIP -> RIGHT_HIP
                RIGHT_HIP -> LEFT_HIP
                LEFT_KNEE -> RIGHT_KNEE
                RIGHT_KNEE -> LEFT_KNEE
                LEFT_ANKLE -> RIGHT_ANKLE
                RIGHT_ANKLE -> LEFT_ANKLE
                else -> keyPoint.bodyPart // NOSE는 그대로 유지
            }
            KeyPoint(
                bodyPart = mirroredBodyPart,
                coordinate = PointF(1f - keyPoint.coordinate.x, keyPoint.coordinate.y),
                score = keyPoint.score
            )
        }.sortedBy { it.bodyPart.position }
    }

    /**
     * 8동작의 비트맵과 관절 좌표를 반환
     */
    private fun extractBestPoseIndices() {
        val jointDataList = jointQueue.toList().reversed()
        imageDataList = imageQueue.toList().reversed()
        var classifier = classifier8
        var modelChangeReady = false

        //수동측정을 위한 값들
        minFinishGap = 100f
        minFollowThroughGap = 100f
        minImpactGap = 100f
        minDownSwingGap = 100f
        minTopOfSwingGap = 100f
        minMidBackSwingGap = 100f
        minTakeAwayGap = 100f
        minAddressGap = 100f
        manualPoseIndexArray = Array(8) { 0 } //수동으로 수치계산하여 선택한 인덱스

        backswingStartTime = 0
        backswingEndTime = 0
        downswingStartTime = 0
        downswingEndTime = 0
        isDownSwingEnd = false

        for ((index, jointData) in jointDataList.withIndex()) {
            // 사람으로 인식안된 프레임의 경우 스킵
            if ((jointData[NOSE.position].score) < 0.3 ||
                (jointData[LEFT_ANKLE.position].score) < 0.3 ||
                (jointData[RIGHT_ANKLE.position].score < 0.3)
            ) {
                continue
            }

            if (!modelChangeReady &&
                jointData[RIGHT_WRIST.position].coordinate.x > jointData[RIGHT_SHOULDER.position].coordinate.x &&
                jointData[RIGHT_WRIST.position].coordinate.y < jointData[RIGHT_SHOULDER.position].coordinate.y
            ) {
                modelChangeReady = true
            }

            // 손목이 어깨 위로 올라가면 모델 교체 && bias 추가
            if (modelChangeReady
                && classifier == classifier8
                && jointData[LEFT_WRIST.position].coordinate.x < jointData[RIGHT_SHOULDER.position].coordinate.x
                && jointData[LEFT_WRIST.position].coordinate.y < jointData[RIGHT_SHOULDER.position].coordinate.y
            ) {
                classifier = classifier4
            }

            if (classifier == classifier8) {

                //피니시 검사
                checkFinish(index, jointData)

                //팔로스루 검사
                checkFollowThrough(index, jointData)

                //임팩트 검사
                checkImpact(index, jointData)

                //다운스윙 검사
                checkDownSwing(index, jointData)

            }

            if (classifier == classifier4) {

                //탑 오브 스윙 검사
                checkTopOfSwing(index, jointData)

                //미드 백 스윙 검사
                checkMidBackSwing(index, jointData)

                //테이크 어웨이 검사
                checkTakeAway(index, jointData)

                //어드레스 검사
                checkAddress(index, jointData)

            }
        }
    }

    private fun analyzeSwingMotion() {
        if (pelvisTwisting) {
            if (swingFrameCount < 5) {
                swingFrameCount++
            } else {
                extractBestPoseIndices()
                if (validateSwingPose(manualPoseIndexArray)) {
                    val poseFrameGroupIndices: Array<IntArray> = Array(8) { IntArray(3) }
                    manualPoseIndexArray.forEachIndexed { index, manualPoseIndex ->
                        poseFrameGroupIndices[index] = when (manualPoseIndex) {
                            0 -> intArrayOf(1, 0, 0)  // 첫 번째 프레임인 경우
                            QUEUE_SIZE - 1 -> intArrayOf(
                                QUEUE_SIZE - 1,
                                QUEUE_SIZE - 1,
                                QUEUE_SIZE - 2
                            )  // 마지막 프레임인 경우
                            else -> intArrayOf(
                                manualPoseIndex + 1,
                                manualPoseIndex,
                                manualPoseIndex - 1
                            )  // 일반적인 경우
                        }
                    }

                    //swingData -> 수동으로 뽑은 8개의 프레임
                    val swingData = indicesToPoses(manualPoseIndexArray)
                    val actualSwingIndices = imageDataList
                        .take(manualPoseIndexArray[0])
                        .map { it.data }
                    //TODO 문현 : 여기서 poseFrameGroup이나 actualSwingIndices 또는 manualPoseIndexArray를 통해서 Similarity를 체크해주자

                    // 템포, 백스윙, 다운스윙 시간 분석하기
                    val swingTiming = analyzeSwingTime(swingData)

                    val backswingTime = String.format(
                        Locale.getDefault(),
                        "%.2f초",
                        swingTiming.backswingTime / 1000.0
                    )
                    val downswingTime = String.format(
                        Locale.getDefault(),
                        "%.2f초",
                        swingTiming.downswingTime / 1000.0
                    )
                    val tempoRatio =
                        String.format(Locale.getDefault(), "%.2f", swingTiming.tempoRatio)

                    // 8개 자세에 대한 이미지 인덱스, 비트맵, 유사도
                    val preciseIndices = swingData.map { it.third }
                    val preciseBitmaps = swingData.map { it.first }
                    val precisePoseScores = classifyPoseScores(swingData.map { it.second })
                    Log.d("유사도", "$precisePoseScores")

                    // 백스윙, 탑스윙 피드백 체크하기
                    val poseAnalysisResults = PostureFeedback.checkPosture(
                        preciseIndices,
                        jointQueue.toList().reversed(),
                        isLeftHanded.not()
                    )
                    Log.d("분석결과", "$poseAnalysisResults")

                    // 나의 스윙 이미지와 전문가의 스윙 이미지 결정하기
                    val userSwingImage: Bitmap
                    val answerSwingImageResId: Int
                    determineSwingImage(poseAnalysisResults, preciseBitmaps).let {
                        answerSwingImageResId = it.first
                        userSwingImage = it.second
                    }

                    // 피드백 다이얼로그 사진 바로 아래에 뜨는 종합 솔루션 결정하기
                    val solution = poseAnalysisResults.solution
                    val solutionComment = solution.getSolution(isLeftHanded.not())

                    // 피드백 다이얼로그 맨 아래에 뜨는 체크리스트 제목과 항목들 결정하기
                    val feedbackCheckList: List<String>
                    val feedbackCheckListTitle: String
                    determineFeedBackCheckList(poseAnalysisResults).let {
                        feedbackCheckList = it.first
                        feedbackCheckListTitle = it.second
                    }

                    val feedBack = FeedBack(
                        downswingTime,
                        tempoRatio,
                        backswingTime,
                        solution == GOOD_SHOT,
                        solutionComment,
                        feedbackCheckListTitle,
                        feedbackCheckList,
                        userSwingImage,
                        answerSwingImageResId
                    )
                    setFeedback(feedBack)

                    // 영상 만들기
                    // TODO: 저장하는 시점과 SSID 이름을 받아서 GETUERIDUSECASE로 회원아이디 받아오고 feedback 객체만들어서 InsertLocalSwingFeedbackUseCase에 던져주면 저장됨
                    // -> 그렇게 하면 suspend 함수를 써야하니까 그냥 local에 저장 또는 login시 받아와서 camerafragment에 전달해줄 수 없나?
                    val fileName = SwingVideoProcessor.saveSwingVideo(context, actualSwingIndices.reversed())
                    // TODO: 영상 + PoseAnalysisResult(솔루션 + 피드백) + @ 룸에 저장하기
                    //아래 객체는 임시로 기능 구현이 가능한지 확인하기 위한 객체로 값은 동적으로 할당해주어야 한다.

                    // TODO 문현 : 8개의 자세에 대해 유사도를 구해서 Similarity를 계한새허 넘겨줘야할거같아요
                    val swingFeedback = SwingFeedback(
                        userID = -1L,
                        videoName = fileName,
                        similarity = Similarity(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0),
                        solution = poseAnalysisResults.solution.getSolution(isLeftHanded.not()),
                        score = 100,
                        tempo = tempoRatio.toDouble(),
                        title = fileName, //score로 주기
                        date = System.currentTimeMillis()
                    )
                    //여기서 이제 swingviewmodel에 swingfeedback객체를 던저줘서 viewmodel에서 비동기로 저장 수행하게 하면 되나?


                    // TODO: 영상 + 8개 비트맵 + 8개 유사도 + 피드백 + @ 서버로 보내기

                    // 스윙 분석 결과 표시 + 결과 표시되는 동안은 카메라 분석 막기
                    increaseSwingCnt()

                    setCurrentCameraState(RESULT)
                    resultSkipMotionStartTime = 0L //스킵 동작 탐지를 위한 변수 초기화
                } else {
                    setCurrentCameraState(AGAIN)
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2500L)
                        setCurrentCameraState(ADDRESS)
                    }
                }
                pelvisTwisting = false
                swingFrameCount = 0
            }
        }
    }

    private fun determineSwingImage(
        poseAnalysisResults: PoseAnalysisResult,
        preciseBitmaps: List<TimestampedData<Bitmap>>,
    ): Pair<Int, Bitmap> {
        val userSwingImage: Bitmap
        val answerSwingImageResId: Int
        when (poseAnalysisResults.solution) {
            BACK_BODY_LIFT -> {
                userSwingImage = preciseBitmaps[TOP.ordinal].data
                answerSwingImageResId = R.drawable.back_body_lift
            }

            BACK_ARM_EXTENSION -> {
                userSwingImage = preciseBitmaps[MID_BACKSWING.ordinal].data
                answerSwingImageResId = R.drawable.back_arm_extension
            }

            BACK_WEIGHT_TRANSFER -> {
                userSwingImage = preciseBitmaps[TOP.ordinal].data
                answerSwingImageResId = R.drawable.back_weight_transfer
            }

            BACK_BODY_BALANCE -> {
                userSwingImage = preciseBitmaps[MID_BACKSWING.ordinal].data
                answerSwingImageResId = R.drawable.back_body_balance
            }

            DOWN_BODY_LIFT -> {
                userSwingImage = preciseBitmaps[MID_DOWNSWING.ordinal].data
                answerSwingImageResId = R.drawable.down_body_lift
            }

            DOWN_WEIGHT_TRANSFER -> {
                userSwingImage = preciseBitmaps[IMPACT.ordinal].data
                answerSwingImageResId = R.drawable.down_weight_transfer
            }

            DOWN_BODY_BALANCE -> {
                userSwingImage = preciseBitmaps[MID_DOWNSWING.ordinal].data
                answerSwingImageResId = R.drawable.down_body_balance
            }

            GOOD_SHOT -> {
                userSwingImage = preciseBitmaps[FINISH.ordinal].data
                answerSwingImageResId = R.drawable.good_shot
            }
        }
        return Pair(answerSwingImageResId, userSwingImage)
    }

    private fun determineFeedBackCheckList(
        poseAnalysisResults: PoseAnalysisResult,
    ): Pair<List<String>, String> {
        val feedbackCheckListTitle: String
        val feedbackCheckList: List<String>
        if (poseAnalysisResults.solution.toString().startsWith("BACK")) {
            feedbackCheckListTitle = context.getString(R.string.back_feedback_title)
            feedbackCheckList =
                context.resources.getStringArray(R.array.back_tip_list).toList()
        } else if (poseAnalysisResults.solution.toString().startsWith("DOWN")) {
            feedbackCheckListTitle = context.getString(R.string.down_feedback_title)
            feedbackCheckList =
                context.resources.getStringArray(R.array.down_tip_list).toList()
        } else {
            feedbackCheckListTitle = context.getString(R.string.good_feedback_title)
            feedbackCheckList =
                context.resources.getStringArray(R.array.good_tip_list).toList()
        }
        return Pair(feedbackCheckList, feedbackCheckListTitle)
    }

    private fun checkFinish(index: Int, jointData: List<KeyPoint>) {
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

    private fun checkFollowThrough(index: Int, jointData: List<KeyPoint>) {
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

    private fun checkImpact(index: Int, jointData: List<KeyPoint>) {
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

    private fun checkDownSwing(index: Int, jointData: List<KeyPoint>) {
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

    private fun checkTopOfSwing(index: Int, jointData: List<KeyPoint>) {
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


    private fun checkMidBackSwing(index: Int, jointData: List<KeyPoint>) {

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

    private fun checkTakeAway(index: Int, jointData: List<KeyPoint>) {
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

    private fun checkAddress(index: Int, jointData: List<KeyPoint>) {
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

    private fun checkResultSkipMotion(jointData: List<KeyPoint>): Boolean {
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

        val deltaX = leftWristX - leftElbowX
        val deltaY = leftElbowY - leftWristY

        val angleRadians = atan2(deltaY, deltaX)
        val angleDegrees = Math.toDegrees(angleRadians.toDouble())

        val currentTime = System.currentTimeMillis()
        Log.d("processDetectedInfo", "RESULT 상태일 때 내부 함수, chckskipmotion 함수 내부 접근")
        if (leftWristX > leftElbowX) {
            val wristElbowDegreeGap = abs(0.0 - angleDegrees).toFloat()
            if (wristElbowDegreeGap < 20.0) {
                resultSkipMotionStartTime = currentTime
                Log.d("processDetectedInfo", "왼쪽 팔뻗음 인식")
            }
        } else if (leftWristX < leftElbowX) {
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

    private fun skipFeedbackDialog() {
        //sendSkipMotionBroadcast
        val intent = Intent().apply {
            action = "SKIP_MOTION_DETECTED"
            putExtra("skipMotion", "Skip motion detected")
        }
        Log.d("processDetectedInfo", "processDetectedInfo: SKIP_MOTION_DETECTED 인텐트 전송 전")
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        Log.d("processDetectedInfo", "processDetectedInfo: SKIP_MOTION_DETECTED 인텐트 전송 후")
    }
}