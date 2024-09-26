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


import VideoEncoder
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Rect
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.PixelCopy
import android.view.SurfaceView
import androidx.annotation.RequiresApi
import com.ijonsabae.presentation.shot.CameraState
import com.ijonsabae.presentation.shot.CameraState.ADDRESS
import com.ijonsabae.presentation.shot.CameraState.AGAIN
import com.ijonsabae.presentation.shot.CameraState.ANALYZING
import com.ijonsabae.presentation.shot.CameraState.POSITIONING
import com.ijonsabae.presentation.shot.CameraState.RESULT
import com.ijonsabae.presentation.shot.CameraState.SWING
import com.ijonsabae.presentation.shot.PostureFeedback
import com.ijonsabae.presentation.shot.SwingViewModel
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
import com.ijonsabae.presentation.shot.ai.data.Pose
import com.ijonsabae.presentation.shot.ai.data.PoseAnalysisResult
import com.ijonsabae.presentation.shot.ai.ml.ModelType
import com.ijonsabae.presentation.shot.ai.ml.MoveNet
import com.ijonsabae.presentation.shot.ai.ml.PoseClassifier
import com.ijonsabae.presentation.shot.ai.ml.PoseDetector
import com.ijonsabae.presentation.shot.ai.ml.TimestampedData
import com.ijonsabae.presentation.shot.ai.utils.VisualizationUtils
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.OutputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.LinkedList
import java.util.Locale
import java.util.Queue
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

data class SwingTiming(
    val backswingTime: Long,
    val downswingTime: Long,
    val totalSwingTime: Long,
    val tempoRatio: Double
)

class CameraSource(
    @ApplicationContext private val context: Context,
    private val getCurrentCameraState: () -> CameraState?,
    private val setCurrentCameraState: (cameraState: CameraState) -> Unit,
    private val updateSwingTiming: (swingTiming: SwingTiming) -> Unit,
    private val setPoseAnalysisResults: (MutableList<PoseAnalysisResult>) -> Unit,
) : CameraSourceListener {
    private var lock = Any()
    private var classifier4: PoseClassifier? = null
    private var classifier8: PoseClassifier? = null
    private var detector: PoseDetector? = null

    private var swingFrameCount = 0
    private var pelvisTwisting = false
    private var viewingResult = false
    private val imageQueue: Queue<TimestampedData<Bitmap>> = LinkedList()
    private val jointQueue: Queue<List<KeyPoint>> = LinkedList()
    private var manualPoseIndexArray = Array(8) { 0 } //수동으로 수치계산하여 선택한 인덱스
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

    private lateinit var surfaceView: SurfaceView

    init {
        // Detector
        val poseDetector = MoveNet.create(context, Device.CPU, ModelType.Lightning)
        setDetector(poseDetector)

        // Classifier
        val classifier4 = PoseClassifier.create(context, MODEL_FILENAME_4, LABELS_FILENAME_4)
        val classifier8 = PoseClassifier.create(context, MODEL_FILENAME_8, LABELS_FILENAME_8)
        setClassifier(classifier4, classifier8)

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

    fun setSurfaceView(surfaceView: SurfaceView){
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

        // 비트맵 회전
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

    fun processImage(bitmap: Bitmap, isSelfCamera: Boolean, isLeftHanded: Boolean) {
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
            surfaceView.holder.unlockCanvasAndPost(canvas)
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

    override fun onDetectedInfo(person: Person) {
        processDetectedInfo(person)
    }

    /** !!!!!!!!!!!! 포즈 추론은 우타, 후면 카메라로 좔영했을 때 기준 !!!!!!!!!!!! **/
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun processDetectedInfo(
        person: Person,
    ) {
        // 결과 분석 보여주는 동안은 이미지 처리 안함
        if (viewingResult) return

        val keyPoints = person.keyPoints
        var isSuccess = false

        // 스윙 피니쉬가 인식된 시점부터 5프레임 더 받기
        if (getCurrentCameraState() == ANALYZING && pelvisTwisting) {
            if (swingFrameCount < 5) {
                swingFrameCount++
            } else {
                val poseIndicesWithScores = extractBestPoseIndices()

                if (validateSwingPose(poseIndicesWithScores)) {
                    val poseFrameGroupIndices: Array<IntArray> = Array(8) { IntArray(3) }
                    poseIndicesWithScores.forEachIndexed { index, frameIndexWithScore ->
                        val frameIndex = frameIndexWithScore.first
                        poseFrameGroupIndices[index] = when (frameIndex) {
                            0 -> intArrayOf(1, 0, 0)  // 첫 번째 프레임인 경우
                            QUEUE_SIZE - 1 -> intArrayOf(
                                QUEUE_SIZE - 1,
                                QUEUE_SIZE - 1,
                                QUEUE_SIZE - 2
                            )  // 마지막 프레임인 경우
                            else -> intArrayOf(
                                frameIndex + 1,
                                frameIndex,
                                frameIndex - 1
                            )  // 일반적인 경우
                        }
                    }
//                    Log.d("싸피", "8개 프레임 추출 완료")
                    val swingData = indicesToPosesGroup(poseFrameGroupIndices)
                    Log.d("싸피", "8개 포즈 그룹(각 3프레임) 추출 완료")
//                    큐에 있는 60개 이미지 갤러리에 전부 저장
//                    imageQueue.toList().forEachIndexed { index, (imageData, _) ->
//                        val fileName = "swing_pose_${index + 1}.jpg"
//                        val uri = saveBitmapToGallery(context, imageData, fileName)
//                        uri?.let {
//                            Log.d("싸피", "Saved image $fileName at $it")
//                        }
//                    }

                    // 8개의 비트맵을 갤러리에 저장
//                    swingData.forEachIndexed { index, (imageData, _) ->
//                        val fileName = "swing_pose_${index + 1}.jpg"
//                        val uri = saveBitmapToGallery(context, imageData.data, fileName)
//                        uri?.let {
//                            Log.d("싸피", "Saved image $fileName at $it")
//                        }
//                    }

//                    // 8개의 포즈 그룹(각 3프레임)을 갤러리에 저장
//                    swingData.forEachIndexed { groupIndex, frameGroup ->
//                        frameGroup.forEachIndexed { _, (imageData, _, originalIndex) ->
//                            val fileName =
//                                "swing_pose_group${groupIndex + 1}_frame${originalIndex + 1}.jpg"
//                            val uri = saveBitmapToGallery(context, imageData.data, fileName)
//                            uri?.let {
//                                Log.d("싸피", "Saved image $fileName at $it")
//                            }
//                        }
//                    }

                    val actualSwingIndices = imageQueue
                        .toList()
                        .takeLast(imageQueue.size - swingData[0][0].third)
                        .map { it.data }

//                  어드레스~피니쉬 이미지 갤러리에 전부 저장
//                    actualSwingIndices.forEachIndexed { idx, bitmap ->
//                        val fileName = "swing_pose_${swingData[0][0].third + idx}.jpg"
//
//                        val uri = saveBitmapToGallery(context, bitmap, fileName)
//                        uri?.let {
//                            Log.d("싸피", "Saved image $fileName at $it")
//                        }
//                    }

                    // TODO: 템포, 백스윙, 다운스윙 시간 분석하기
                    updateSwingTiming(analyzeSwingTime(swingData))
                    // TODO: 피드백 분석하기
                    val extractedKeyPoints = swingData.map { outerList ->
                        outerList.map { triple ->
                            Pair(triple.first.data, triple.second)
                        }
                    }
                    val poseAnalysisResults = PostureFeedback.checkPosture(extractedKeyPoints)

                    setPoseAnalysisResults(poseAnalysisResults)

                    // 8개의 베스트 포즈에 대한 비트맵을 갤러리에 저장
                    poseAnalysisResults.forEachIndexed { idx, result ->
                        val fileName =
                            "swing_pose_group${idx + 1}_frame.jpg"
                        val uri = saveBitmapToGallery(context, result.bitmap, fileName)
                        uri?.let {
                            Log.d("싸피", "Saved image $fileName at $it")
                        }
                    }

                    // TODO: 템포, 백스윙, 다운스윙 시간 분석하기

                    // TODO: 피드백 분석하기

                    // TODO: 영상 만들기
                    convertBitmapsToVideo(actualSwingIndices)
                    // TODO: 영상과 피드백 룸에 저장하기

                    // TODO: 영상 + 피드백 서버에 저장하기 (비동기) <- 나중에 여기서 보낸 피드백을 토대로 종합 리포트 만들어줄 예정

                    // TODO: 스윙 분석 결과 표시
                    setCurrentCameraState(RESULT)
                    isSuccess = true
                } else {
                    setCurrentCameraState(AGAIN)
                    Log.d("싸피", "다시 스윙해주세요")
                    isSuccess = false
                }
                viewingResult = true

                val delayTime = if (isSuccess) 4500L else 2800L
                CoroutineScope(Dispatchers.Main).launch {
                    delay(delayTime)
                    viewingResult = false
                    pelvisTwisting = false
                    swingFrameCount = 0
                    setCurrentCameraState(ADDRESS)
                }
            }
            return
        }

        // 5. 스윙의 마지막 동작 체크
        if (getCurrentCameraState() == SWING) {
            // 오른어깨와 왼발이 가까워지면
            if (abs(keyPoints[RIGHT_SHOULDER.position].coordinate.x - keyPoints[LEFT_ANKLE.position].coordinate.x) < 0.05f &&
                abs(keyPoints[RIGHT_SHOULDER.position].coordinate.y - keyPoints[RIGHT_ANKLE.position].coordinate.y) > 0.3f &&
                keyPoints[RIGHT_ELBOW.position].coordinate.x > keyPoints[RIGHT_SHOULDER.position].coordinate.x
            ) {
                pelvisTwisting = true
                setCurrentCameraState(ANALYZING)
                Log.d("싸피", "피니쉬 인식 완료")
            }
            return
        }

        // 4. 스윙하는 동안은 안내 메세지 안변하도록 유지
        if (getCurrentCameraState() == SWING &&
            ((keyPoints[LEFT_WRIST.position].coordinate.x < keyPoints[LEFT_SHOULDER.position].coordinate.x) ||
                    (keyPoints[RIGHT_WRIST.position].coordinate.x > keyPoints[RIGHT_SHOULDER.position].coordinate.x)).not()
        ) {
            setCurrentCameraState(ADDRESS)
            return
        }

        // 1. 몸 전체가 카메라 화면에 들어오는지 체크
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
    }

    private fun indicesToPoses(indices: List<Int>): MutableList<Pair<TimestampedData<Bitmap>, List<KeyPoint>>> {
        val poses = mutableListOf<Pair<TimestampedData<Bitmap>, List<KeyPoint>>>()
        val jointList = jointQueue.toList().reversed()
        val imageList = imageQueue.toList().reversed()
        for (index in indices) {
            poses.add(Pair(imageList[index], jointList[index]))
        }
        return poses
    }

    private fun indicesToPosesGroup(poseFrameGroupIndices: Array<IntArray>): List<List<Triple<TimestampedData<Bitmap>, List<KeyPoint>, Int>>> {
        val poses = mutableListOf<List<Triple<TimestampedData<Bitmap>, List<KeyPoint>, Int>>>()
        val jointList = jointQueue.toList().reversed()
        val imageList = imageQueue.toList().reversed()
        for (group in poseFrameGroupIndices) {
            val groupPoses = group.map { index ->
                Triple(imageList[index], jointList[index], QUEUE_SIZE - 1 - index)
            }
            poses.add(groupPoses)
        }
        return poses
    }

    fun analyzeSwingTime(poses: List<List<Triple<TimestampedData<Bitmap>, List<KeyPoint>, Int>>>): SwingTiming {
        //이상적인 템포 비율은 약 3:1(백스윙:다운스윙)로 알려져 있지만, 개인의 스타일과 체형에 따라 다를 수 있다.

        //1. 피니시, 임팩트, 탑스윙, 어드레스 ~ 테이크 어웨이 자세에 대한 Long값 추출
        val finishTime = poses[7][1].first.timestamp
        val impactTime = poses[5][1].first.timestamp
        val topTime = poses[3][1].first.timestamp
        val addressTime = backswingStartTime
        //2. 전체 스윙 시간, 백스윙, 다운스윙 추출
        val backswingTime = topTime - addressTime
        val downswingTime = impactTime - topTime
        //3. 전체 스윙 시간, 백스윙 시간, 다운스윙 시간 반환
        val totalSwingTime = finishTime - addressTime

        val tempoRatio = backswingTime.toDouble() / downswingTime.toDouble()

        return SwingTiming(
            backswingTime = backswingTime,
            downswingTime = downswingTime,
            totalSwingTime = totalSwingTime,
            tempoRatio = tempoRatio
        )

    }

    private fun validateSwingPose(poseIndicesWithScores: List<Pair<Int, Float>>): Boolean {
        val countingArray = BooleanArray(QUEUE_SIZE) { false }
        var prevImageIndex = 100_000_000

        var i = 0
        for (indexWithScore in poseIndicesWithScores) {
            val index = indexWithScore.first
            val score = indexWithScore.second

            // 포즈가 일정 유사도를 넘어야 정상 스윙으로 판단
            if (score < POSE_THRESHOLD) {
                Log.d("포즈검증", "${Pose.entries[i]} 유사도 미충족!! index: $index, score: $score")
                return false
            }

            // 이미지 인덱스에 중복이 없어야 정상
            if (!countingArray[index]) {
                countingArray[index] = true
            } else {
                Log.d("포즈검증", "중복발생!! index: $index, score: $score")
                return false
            }

            // 이미지 순서가 맞아야 정상
            if (index >= prevImageIndex) {
                Log.d("포즈검증", "이미지 순서가 맞지 않음!!")
                return false
            }
            prevImageIndex = index
            i++
        }
        return true
    }


    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/SwingAnalysis"
            )
        }

        var uri: Uri? = null
        try {
            uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            uri?.let {
                val outputStream: OutputStream? = context.contentResolver.openOutputStream(it)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                }
            }
        } catch (e: Exception) {
            Log.e("싸피", "Error saving bitmap: ${e.message}")
        }

        return uri
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun convertBitmapsToVideo(bitmapIndices: List<Bitmap>) {


        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val videoFileName = "pose_$timestamp.mp4"
        val videoFile = File(context.cacheDir, videoFileName)

        val videoEncoder = VideoEncoder(
            bitmapIndices[0].width,
            bitmapIndices[0].height,
            24,
            videoFile.absolutePath
        )
        videoEncoder.start()

        bitmapIndices.forEachIndexed { index, bitmap ->
            val byteBuffer = bitmapToByteBuffer(bitmap)
            Log.d("MainActivity_Capture", "프레임 인덱스: $index")
            videoEncoder.encodeFrame(byteBuffer)
        }

        videoEncoder.finish()

        val uri = saveVideoToGallery(videoFile)
        uri?.let {
            Log.d("MainActivity_Capture", "비디오 URI: $it")
            MediaScannerConnection.scanFile(context, arrayOf(it.toString()), null, null)


        } ?: Log.d("MainActivity_Capture", "비디오 저장 실패")

        // 임시 파일 삭제
        videoFile.delete()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveVideoToGallery(videoFile: File): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, videoFile.name)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(
                MediaStore.Video.Media.RELATIVE_PATH,
                Environment.DIRECTORY_MOVIES + "/PoseEstimation"
            ) // 수정된 부분
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(collection, values)

        uri?.let {
            resolver.openOutputStream(it)?.use { os ->
                videoFile.inputStream().use { it.copyTo(os) }
            }
            values.clear()
            values.put(MediaStore.Video.Media.IS_PENDING, 0)
            resolver.update(it, values, null, null)
        }

        return uri
    }

    private fun bitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val inputWidth = bitmap.width
        val inputHeight = bitmap.height
        val yuvImage = ByteArray(inputWidth * inputHeight * 3 / 2)
        val argb = IntArray(inputWidth * inputHeight)

        bitmap.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight)

        encodeYUV420SP(yuvImage, argb, inputWidth, inputHeight)

        return ByteBuffer.wrap(yuvImage)
    }


    private fun encodeYUV420SP(yuv420sp: ByteArray, argb: IntArray, width: Int, height: Int) {
        val frameSize = width * height
        var yIndex = 0
        var uvIndex = frameSize

        for (j in 0 until height) {
            for (i in 0 until width) {
                val rgb = argb[j * width + i]
                val r = rgb shr 16 and 0xFF
                val g = rgb shr 8 and 0xFF
                val b = rgb and 0xFF
                val y = (66 * r + 129 * g + 25 * b + 128 shr 8) + 16
                yuv420sp[yIndex++] = y.toByte()

                if (j % 2 == 0 && i % 2 == 0) {
                    val u = (-38 * r - 74 * g + 112 * b + 128 shr 8) + 128
                    val v = (112 * r - 94 * g - 18 * b + 128 shr 8) + 128
                    yuv420sp[uvIndex++] = u.toByte()
                    yuv420sp[uvIndex++] = v.toByte()
                }
            }
        }
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
    private fun extractBestPoseIndices(): List<Pair<Int, Float>> {
        val jointDataList = jointQueue.toList().reversed()
        val imageDataList = imageQueue.toList().reversed()
        var poseLabelBias = 4
        var classifier = classifier8
        var modelChangeReady = false
        val poseIndexArray = Array(8) { Pair(0, 0f) } //모델 스코어로 추론한 인덱스
        manualPoseIndexArray = Array(8) { 0 } //수동으로 수치계산하여 선택한 인덱스



        //수동측정을 위한 값들
        var wristHipDist = 1f
        var minFollowThroughGap = 1f
        var minImpactGap = 1f

        for ((index, jointData) in jointDataList.withIndex()) {
            if (!modelChangeReady &&
                jointData[RIGHT_WRIST.position].coordinate.x > jointData[RIGHT_SHOULDER.position].coordinate.x &&
                jointData[RIGHT_WRIST.position].coordinate.y < jointData[RIGHT_SHOULDER.position].coordinate.y
            ) {
                Log.d("포즈검증", "모델 교체 준비")
                modelChangeReady = true
            }

            // 손목이 어깨 위로 올라가면 모델 교체 && bias 추가
            if (modelChangeReady
                && classifier == classifier8
                && jointData[LEFT_WRIST.position].coordinate.x < jointData[RIGHT_SHOULDER.position].coordinate.x
                && jointData[LEFT_WRIST.position].coordinate.y < jointData[RIGHT_SHOULDER.position].coordinate.y
            ) {
                Log.d("포즈검증", "모델 교체 완료")
                classifier = classifier4
                poseLabelBias = 0
            }

            val classificationResults = classifier?.classify(jointData)
            classificationResults?.forEachIndexed { poseIndex, result ->
                if (result.second > poseIndexArray[poseIndex + poseLabelBias].second) {
                    poseIndexArray[poseIndex + poseLabelBias] = Pair(index, result.second)
                }
            }

            //backswing 시작 시간 추적을 위한 로직
            if (classifier == classifier4) {
                val hipWristDistancePow =
                    (jointData[RIGHT_HIP.position].coordinate.x - jointData[RIGHT_WRIST.position].coordinate.x).pow(
                        2
                    ) +
                            (jointData[RIGHT_HIP.position].coordinate.y - jointData[RIGHT_WRIST.position].coordinate.y).pow(
                                2
                            )
                if (hipWristDistancePow < wristHipDist) {
                    wristHipDist = hipWristDistancePow
                    backswingStartTime = imageDataList[index].timestamp
//                    Log.d("extractBestPoseIndices", "백스윙 시작시간 인덱스 : ${index} , $backswingStartTime")
                }


                //피니쉬 - 팔로스루 - 임팩트 - 다운스윙
                //피니쉬 - checkFinish()

                //팔로스루 인덱스 갱신
                minFollowThroughGap = min(checkFollowThrough(index, jointData, minFollowThroughGap), minFollowThroughGap)

                //임팩트 인덱스 갱신
                if (jointData[LEFT_HIP.position].coordinate.y >= jointData[LEFT_WRIST.position].coordinate.y)
                    minImpactGap = min(checkImpact(index, jointData, minImpactGap), minImpactGap)






            }
        }

        return poseIndexArray.toList()
    }

    private fun checkFollowThrough(index: Int, jointData: List<KeyPoint>, minFollowThroughGap: Float) : Float {
        //왼손목과 오른골반의 높이 이상이면서 가장 가까울때
        val followThroughGap = abs(jointData[LEFT_WRIST.position].coordinate.y - jointData[RIGHT_HIP.position].coordinate.y)
        if (
            (jointData[LEFT_WRIST.position].coordinate.y > jointData[RIGHT_HIP.position].coordinate.y) and
            (minFollowThroughGap > followThroughGap)
        ) {
            manualPoseIndexArray[6] = index
        }
        return followThroughGap
    }

    private fun checkImpact(index: Int, jointData: List<KeyPoint>, minImpactGap: Float): Float {
        //임팩트 - 손목의 평균 좌표가 골반의 중앙과 가장 가까울 때
        var hipCenterXCoord = (jointData[RIGHT_HIP.position].coordinate.x + jointData[LEFT_HIP.position].coordinate.x) / 2
        var impactGap = abs(hipCenterXCoord - (jointData[RIGHT_WRIST.position].coordinate.x + jointData[LEFT_WRIST.position].coordinate.x)/2)
        if (minImpactGap > impactGap) {
            manualPoseIndexArray[5] = index
        }
        return impactGap
    }


    private var lastLogTime = 0L

    private fun logWithThrottle(message: String) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastLogTime >= 1000) { // 1초 이상 지났는지 확인
            Log.d("싸피", message)
            lastLogTime = currentTime
        }
    }
}