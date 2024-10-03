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
import android.content.Intent
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
import java.io.File
import java.io.OutputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date
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
    val totalSwingTime: Long,
    val tempoRatio: Double
)

class CameraSource(
    @ApplicationContext private val context: Context,
    private val isLeftHanded: Boolean,
    private val getCurrentCameraState: () -> CameraState?,
    private val setCurrentCameraState: (cameraState: CameraState) -> Unit,
    private val setFeedback: (FeedBack) -> Unit,
    private val initializeSwingCnt: () -> Unit,
    private val increaseSwingCnt: () -> Unit
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

    private var resultSkipMotionStartTime : Long = 0L //스윙결과 스킵 동작을 인식하기 위한 변수

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

        initializeSwingCnt.invoke()
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

    @RequiresApi(Build.VERSION_CODES.Q)
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

    /** !!!!!!!!!!!! 포즈 추론은 우타, 후면 카메라로 좔영했을 때 기준 !!!!!!!!!!!! **/
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun processDetectedInfo(
        person: Person
    ) {
        val keyPoints = person.keyPoints
        val currentState = getCurrentCameraState()
        when (currentState) {
            RESULT -> {                    /**
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
            else -> {
                if (currentState == SWING) {
                    // 오른어깨와 왼발이 가까워지면
                    if (abs(keyPoints[RIGHT_SHOULDER.position].coordinate.x - keyPoints[LEFT_ANKLE.position].coordinate.x) < 0.05f &&
                        abs(keyPoints[RIGHT_SHOULDER.position].coordinate.y - keyPoints[RIGHT_ANKLE.position].coordinate.y) > 0.3f &&
                        keyPoints[RIGHT_ELBOW.position].coordinate.x > keyPoints[RIGHT_SHOULDER.position].coordinate.x
                    ) {
                        pelvisTwisting = true
                        setCurrentCameraState(ANALYZING)
                        Log.d("싸피", "피니쉬 인식 완료")
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

    private fun analyzeSwingTime(poses: List<Triple<TimestampedData<Bitmap>, List<KeyPoint>, Int>>): SwingTiming {
        //이상적인 템포 비율은 약 3:1(백스윙:다운스윙)로 알려져 있지만, 개인의 스타일과 체형에 따라 다를 수 있다.
        val finishTime = poses[7].first.timestamp
        val backswingTime = backswingEndTime - backswingStartTime
        val downswingTime = downswingEndTime - downswingStartTime

        val totalSwingTime = finishTime - backswingStartTime

        val tempoRatio = backswingTime.toDouble() / downswingTime.toDouble()

        // TODO: backswingEndTime - backswingStartTime가 음수로 나오는 현상 수정하기

        Log.d("타이밍", "${manualPoseIndexArray.toList()}")
        Log.d("타이밍", "1: $backswingEndTime, $backswingStartTime, $backswingTime")
        Log.d("타이밍", "2: $downswingEndTime, $downswingStartTime, $downswingTime")


        return SwingTiming(
            backswingTime = backswingTime,
            downswingTime = downswingTime,
            totalSwingTime = totalSwingTime,
            tempoRatio = tempoRatio
        )
    }

    private fun validateSwingPose(poseIndices: Array<Int>): Boolean {
        Log.d("분석결과", "파라미터 : ${Arrays.toString(poseIndices)}")


        // 중복 검사
        val uniqueIndices = poseIndices.toSet()
        if (uniqueIndices.size != poseIndices.size) return false

        // 지속적으로 감소하는지 검사 -> 순서 보장
        for (i in 0 until poseIndices.size - 1) {
            if (poseIndices[i] <= poseIndices[i + 1]) {
                return false
            }
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


    //    @RequiresApi(Build.VERSION_CODES.Q)
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

    //    @RequiresApi(Build.VERSION_CODES.Q)
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
    private fun extractBestPoseIndices() {
        val jointDataList = jointQueue.toList().reversed()
        imageDataList = imageQueue.toList().reversed()
        var poseLabelBias = 4
        var classifier = classifier8
        var modelChangeReady = false
        val poseIndexArray = Array(8) { Pair(0, 0f) } //모델 스코어로 추론한 인덱스

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

//            val classificationResults = classifier?.classify(jointData)
//            classificationResults?.forEachIndexed { poseIndex, result ->
//                if (result.second > poseIndexArray[poseIndex + poseLabelBias].second) {
//                    poseIndexArray[poseIndex + poseLabelBias] = Pair(index, result.second)
//                }
//            }

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
                    //swingGroupData -> 전후 프레임까지 포함한 묶음
                    val swingGroupData = indicesToPosesGroup(poseFrameGroupIndices)


                    //수동으로 뽑은 이미지 포즈들을 기반으로 첫 시작 시간 추정 후 영상 제작
                    val actualSwingIndices = imageQueue
                        .toList()
                        .takeLast(imageQueue.size - manualPoseIndexArray[0])
                        .map { it.data }


                    // 템포, 백스윙, 다운스윙 시간 분석하기
                    val swingTiming = analyzeSwingTime(swingData)
                    val backswingTime =
                        String.format(
                            Locale.getDefault(),
                            "%.2f",
                            swingTiming.backswingTime / 1000.0
                        ).toFloat()
                    val downswingTime =
                        String.format(
                            Locale.getDefault(),
                            "%.2f",
                            swingTiming.downswingTime / 1000.0
                        ).toFloat()
                    val tempoRatio =
                        String.format(Locale.getDefault(), "%.2f", swingTiming.tempoRatio)
                            .toFloat()

                    // 백스윙, 탑스윙 피드백 체크하기
                    val preciseIndices = swingData.map { it.third }
                    val preciseBitmaps = swingData.map { it.first }
                    val precisePoseScores = classifyPoseScores(swingData.map { it.second })

                    val poseAnalysisResults = PostureFeedback.checkPosture(
                        preciseIndices,
                        jointQueue.toList().reversed(),
                        true // TODO: 좌타 우타 여부 동적으로 넣어주기
                    )
                    Log.d("분석결과", "$poseAnalysisResults")

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


                    // 뷰모델에 피드백 담기
                    val feedBack = FeedBack(
                        downswingTime,
                        tempoRatio,
                        backswingTime,
                        poseAnalysisResults.solution.getSolution(true), // TODO: 좌타 우타 여부 동적으로 넣어주기
                        feedbackCheckListTitle,
                        feedbackCheckList
                    )
                    setFeedback(feedBack)

                    // 8개의 베스트 포즈에 대한 비트맵을 갤러리에 저장
//                    poseAnalysisResults.forEachIndexed { idx, result ->
//                        val fileName =
//                            "swing_pose_group${idx + 1}_frame.jpg"
//                        val uri = saveBitmapToGallery(context, result.bitmap, fileName)
//                        uri?.let {
//                            Log.d("싸피", "Saved image $fileName at $it")
//                        }
//                    }

//                    Log.d("수동측정", "수동측정이미지 inx : ${Arrays.toString(manualPoseIndexArray)}")
//                    // 8개의 수동 측정 포즈에 대한 비트맵을 갤러리에 저장
//                    manualPoseIndexArray.forEachIndexed { idx, poseImageIndex ->
//                        val fileName = "swing_pose_group${idx + 1}_frame.jpg"
//                        val uri = saveBitmapToGallery(context, imageDataList[poseImageIndex].data, fileName)
//                        uri?.let {
////                            Log.d("수동측정", "수동측정이미지 저장 ${idx + 1} frameidx_${poseImageIndex}_frame.jpg")
//                        }
//                    }

                    // 영상 만들기`
                    convertBitmapsToVideo(actualSwingIndices)

                    // TODO: 영상 + PoseAnalysisResult(솔루션 + 피드백) 룸에 저장하기

                    // TODO: 영상 + 8개 비트맵 + 8개 유사도 + 피드백 서버로 보내기

                    // 스윙 분석 결과 표시 + 결과 표시되는 동안은 카메라 분석 막기
                    increaseSwingCnt.invoke()
                    setCurrentCameraState(RESULT)
                    Log.d("processDetectedInfo", "상태 Result로 변경됨")
                    resultSkipMotionStartTime = 0L //스킵 동작 탐지를 위한 변수 초기화

                    // TODO: 다이얼로그가 닫히는 순간 swingViewModel.currentState 바꿔주기

                } else {
                    setCurrentCameraState(AGAIN)
                    Log.d("싸피", "다시 스윙해주세요")
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
        val leftHipY = jointData[LEFT_HIP.position].coordinate.y
        val rightHipX = jointData[RIGHT_HIP.position].coordinate.x

        val leftWristX = jointData[LEFT_WRIST.position].coordinate.x
        val leftWristY = jointData[LEFT_WRIST.position].coordinate.y

        //손목이 골반 아래 위치할 때 골반 중심과 x좌표 거리가 가장 가까운 경우를 추출

        if (leftHipY <= leftWristY) {
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


        // TODO : 문현 - 여기 시간 이랑 인덱스 문제 해결하기
        //코보다 왼손 높이가 커지는 시점을 갱신
        if (!isDownSwingEnd && leftWristY < noseY && leftWristX < noseX) {
            downswingStartTime = imageDataList[index - 1].timestamp
            isDownSwingEnd = true
        }
        if (isDownSwingEnd && leftWristY >= noseY && leftWristX < noseX) {
            backswingEndTime = imageDataList[index - 1].timestamp
            isDownSwingEnd = false
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
        val leftWristX = jointData[LEFT_WRIST.position].coordinate.x
        val leftWristY = jointData[LEFT_WRIST.position].coordinate.y

        if (leftWristY > rightHipY) {
            // 거리 계산을 위해 제곱근을 사용
            val hipWristDistance = sqrt(
                (rightHipX - leftWristX).pow(2) +
                        (rightHipY - leftWristY).pow(2)
            )

            // minAddressGap이 거리보다 큰 경우에만 업데이트
            if (minAddressGap > hipWristDistance) {
                minAddressGap = hipWristDistance
                if (index + 1 <= imageDataList.size - 1) {
                    backswingStartTime = imageDataList[index + 1].timestamp // 스윙 시작시간 갱신
                    manualPoseIndexArray[0] = index + 1
                } else {
                    backswingStartTime = imageDataList[index].timestamp // 스윙 시작시간 갱신
                    manualPoseIndexArray[0] = index
                }
            }
        }
    }

    private fun checkResultSkipMotion(jointData: List<KeyPoint>) : Boolean{
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