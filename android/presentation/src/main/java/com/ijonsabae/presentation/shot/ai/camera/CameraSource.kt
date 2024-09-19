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


import MoveNet
import TimestampedData
import android.content.ContentValues
import com.ijonsabae.presentation.shot.ai.ml.PoseClassifier
import com.ijonsabae.presentation.shot.ai.ml.PoseDetector
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Rect
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.PixelCopy
import android.view.SurfaceView
import com.ijonsabae.presentation.shot.CameraState.ADDRESS
import com.ijonsabae.presentation.shot.CameraState.ANALYZING
import com.ijonsabae.presentation.shot.CameraState.POSITIONING
import com.ijonsabae.presentation.shot.CameraState.SWING
import com.ijonsabae.presentation.shot.SwingViewModel
import com.ijonsabae.presentation.shot.ai.data.BodyPart.*
import com.ijonsabae.presentation.shot.ai.data.Device
import com.ijonsabae.presentation.shot.ai.data.KeyPoint
import com.ijonsabae.presentation.shot.ai.data.Person
import com.ijonsabae.presentation.shot.ai.utils.VisualizationUtils
import java.io.OutputStream
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.CountDownLatch
import kotlin.math.max


class CameraSource(
    private val context: Context,
    private val swingViewModel: SwingViewModel,
    private val surfaceView: SurfaceView
) : CameraSourceListener {
    private var lock = Any()
    private var classifier4: PoseClassifier? = null
    private var classifier8: PoseClassifier? = null
    private var detector: PoseDetector? = null

    private var startDetectionOfFinish = false
    private val imageQueue: Queue<TimestampedData<Bitmap>> = LinkedList()
    private val jointQueue: Queue<List<KeyPoint>> = LinkedList()


    init {
        // Detector
        val poseDetector = MoveNet.create(context, Device.NNAPI, ModelType.Lightning)
        setDetector(poseDetector)

        // Classifier
        val classifier4 = PoseClassifier.create(context, MODEL_FILENAME_4, LABELS_FILENAME_4)
        val classifier8 = PoseClassifier.create(context, MODEL_FILENAME_8, LABELS_FILENAME_8)
        setClassifier(classifier4, classifier8)

    }

    companion object {
        /** Threshold for confidence score. */
        private const val MIN_CONFIDENCE = .2f
        private const val TAG = "Camera Source"

        /** Classifier */
        private const val MODEL_FILENAME_4 = "pose_classifier_4.tflite"
        private const val LABELS_FILENAME_4 = "labels4.txt"
        private const val MODEL_FILENAME_8 = "pose_classifier_8.tflite"
        private const val LABELS_FILENAME_8 = "labels8.txt"

        private const val QUEUE_SIZE = 100
    }

    /** Frame count that have been processed so far in an one second interval to calculate FPS. */
    private var frameProcessedInOneSecondInterval = 0
    private var framesPerSecond = 1

    private var frameCount = 0
    private val TARGET_FPS = 60

    /** [HandlerThread] where all buffer reading operations run */
    private var imageReaderThread: HandlerThread? = null

    /** [Handler] corresponding to [imageReaderThread] */
    private var imageReaderHandler: Handler? = null

    fun getQueuedData(): Pair<List<Pair<Bitmap, Long>>, List<List<KeyPoint>>> {
        val images = imageQueue.toList().map { Pair(it.data, it.timestamp) }
        val joints = jointQueue.toList()
        return Pair(images, joints)
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

    fun processImage(bitmap: Bitmap, isSelf: Boolean) {
        frameCount++

        // framesPerSecond가 0이거나 TARGET_FPS보다 작으면 모든 프레임을 처리합니다.
        val shouldProcessFrame =
            framesPerSecond <= TARGET_FPS || frameCount % max(1, framesPerSecond / TARGET_FPS) == 0

        if (shouldProcessFrame) {
            var poseResult: Person?

            synchronized(lock) {
                poseResult = detector?.estimatePoses(bitmap)
                poseResult?.let {
                    visualize(it, bitmap)

                    // 관절 그러진 비트맵 큐에 넣기
                    val capturedBitmap = captureSurfaceView(surfaceView)
                    capturedBitmap?.let {
                        val currentTime = System.currentTimeMillis()
                        if (imageQueue.size >= QUEUE_SIZE) {
                            imageQueue.poll()
                        }
                        imageQueue.offer(TimestampedData(capturedBitmap, currentTime))
                    }

                    // 패딩된 관절을 imageQueue에 추가합니다.
                    if (jointQueue.size >= QUEUE_SIZE) {
                        jointQueue.poll()
                    }
                    jointQueue.offer(it.keyPoints) // 큐의 맨 뒤에 새 비트맵 추가

                    onDetectedInfo(it)
                }
            }

            frameProcessedInOneSecondInterval++
        } else {
            Log.d(TAG, "processImage: 처리 안함")
        }
    }

    private fun captureSurfaceView(surfaceView: SurfaceView): Bitmap? {
        val bitmap =
            Bitmap.createBitmap(surfaceView.width, surfaceView.height, Bitmap.Config.ARGB_8888)
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

    /****************************************************************************************/

    override fun onDetectedInfo(person: Person) {
        processDetectedInfo(person, isSelf = false)
    }

    /**
     * TODO: isSelf(카메라 방향), isLeft(좌타여부) 받아서 넣어주기, 지금은 하드코딩
     * estimatePoses()에서 뒤집으면 그릴 때도 반대로 그려줘서 추론때만 반대로 써서 판단해야 해요
     * !!!!!!!!!!!! 포즈 추론은 우타, 후면 카메라로 좔영했을 때 기준 !!!!!!!!!!!!
     **/
    private fun processDetectedInfo(
        person: Person,
        isSelf: Boolean = false,
        isLeft: Boolean = false
    ) {
        val keyPoints = if (isSelf != isLeft) {
            mirrorKeyPoints(person.keyPoints)
        } else {
            person.keyPoints
        }

        // 5. 스윙의 마지막 동작 체크
        if (swingViewModel.currentState.value == SWING) {
            if (startDetectionOfFinish) {
                // if 코보다 손목 y 좌표가 내려가면 손이 3,4분면에 내려가는 거니까 다시 startdof를 false로 해준다.
                if (keyPoints[NOSE.position].coordinate.y < keyPoints[RIGHT_WRIST.position].coordinate.y) {
                    startDetectionOfFinish = false
                    swingViewModel.setCurrentState(ADDRESS)
                }
                // elif 손목 x 좌표가 코보다 작아지면 2사분면으로 이동한거니까 startdof를 false로 하고 스윙 추출을 시작한다.
                else if (keyPoints[RIGHT_WRIST.position].coordinate.x < keyPoints[NOSE.position].coordinate.x) {
                    swingViewModel.setCurrentState(ANALYZING)
                    val swingData = extractSwing()

                    if (swingData.size == 8) {
                        Log.d("싸피", "@@ 프레임 분석 완료")

                        // 8개의 비트맵을 갤러리에 저장
                        swingData.forEachIndexed { index, (imageData, _) ->
                            val fileName = "swing_pose_${index + 1}.jpg"
                            val uri = saveBitmapToGallery(context, imageData.data, fileName)
                            uri?.let {
                                Log.d("싸피", "Saved image $fileName at $it")
                            }
                        }

                        // TODO: 템포, 백스윙, 다운스윙 시간 분석하기

                        // TODO: 영상 메모리에 올리기

                        // TODO: 영상 서버에 저장하기 (비동기)

                        // TODO: 스윙 분석 결과 표시 (일정시간)

                    } else {
                        // TODO: 다시 스윙해주세요 표시 (일정시간)
                        Log.d("싸피", "@@ 다시 스윙해주세요, ${swingData.size}")
                    }
                    startDetectionOfFinish = false
                    swingViewModel.setCurrentState(ADDRESS)
                }
            } else {
                if (keyPoints[RIGHT_WRIST.position].coordinate.x > keyPoints[NOSE.position].coordinate.x &&
                    keyPoints[RIGHT_WRIST.position].coordinate.y < keyPoints[NOSE.position].coordinate.y
                )
                    startDetectionOfFinish = true
            }
        }

        // 4. 스윙하는 동안은 안내 메세지 안변하도록 유지
        if (swingViewModel.currentState.value == SWING &&
            ((keyPoints[LEFT_WRIST.position].coordinate.x < keyPoints[LEFT_SHOULDER.position].coordinate.x) ||
                    (keyPoints[RIGHT_WRIST.position].coordinate.x > keyPoints[RIGHT_SHOULDER.position].coordinate.x))
        ) return

        // 1. 몸 전체가 카메라 화면에 들어오는지 체크
        if ((keyPoints[NOSE.position].score) < 0.3 ||
            (keyPoints[LEFT_ANKLE.position].score) < 0.3 ||
            (keyPoints[RIGHT_ANKLE.position].score < 0.3)
        ) {
            swingViewModel.setCurrentState(POSITIONING)
        }
        // 2. 어드레스 자세 체크
        else if ((keyPoints[LEFT_WRIST.position].coordinate.y > keyPoints[LEFT_ELBOW.position].coordinate.y &&
                    keyPoints[RIGHT_WRIST.position].coordinate.y > keyPoints[RIGHT_ELBOW.position].coordinate.y &&
                    keyPoints[LEFT_WRIST.position].coordinate.x <= keyPoints[LEFT_SHOULDER.position].coordinate.x &&
                    keyPoints[LEFT_WRIST.position].coordinate.x >= keyPoints[RIGHT_SHOULDER.position].coordinate.x &&
                    keyPoints[RIGHT_WRIST.position].coordinate.x >= keyPoints[RIGHT_SHOULDER.position].coordinate.x &&
                    keyPoints[RIGHT_WRIST.position].coordinate.x <= keyPoints[LEFT_SHOULDER.position].coordinate.x).not()
        ) {
            swingViewModel.setCurrentState(ADDRESS)
        }
        // 3. 스윙해주세요!
        else {
            swingViewModel.setCurrentState(SWING)
        }
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
    private fun extractSwing(): MutableList<Pair<TimestampedData<Bitmap>, List<KeyPoint>>> {
        val bitmapAndKeyPoint = mutableListOf<Pair<TimestampedData<Bitmap>, List<KeyPoint>>>()
        val imageDataList = imageQueue.toList().reversed()
        val jointDataList = jointQueue.toList().reversed()

        // 8가지 자세 리스트 (finish부터 address까지 역순)
        val poseLabels = listOf(
            "finish",
            "mid-follow-through",
            "impact",
            "mid-downswing",
            "top",
            "mid-backswing",
            "toe-up",
            "address"
        )

        var currentPoseIndex = 0
        var bestFrameForCurrentPose: Pair<TimestampedData<Bitmap>, List<KeyPoint>>? = null
        var lastScore = 0f
        var poseIdx = 0
        imageDataList.zip(jointDataList).forEach { (imageData, jointData) ->
            val currentLabel = poseLabels[currentPoseIndex]
            val classifier = if (currentPoseIndex < 4) classifier8 else classifier4

            val (predictedLabel, score) = classifier?.classify(jointData) ?: Pair("", 0f)

            Log.d(
                "싸피_라벨 및 점수",
                "${poseIdx++} - $currentPoseIndex $predictedLabel, ${
                    String.format(
                        "%.1f",
                        score * 100
                    )
                }%"
            )

            if (predictedLabel == currentLabel && score > lastScore) {
                bestFrameForCurrentPose = Pair(imageData, jointData)
                lastScore = score
            } else if (predictedLabel != currentLabel || (imageData == imageDataList.last() && bestFrameForCurrentPose != null)) {
                if (lastScore > 0.3) {  // 임계값 체크
                    bestFrameForCurrentPose?.let {
                        bitmapAndKeyPoint.add(it)
                        currentPoseIndex++
                        bestFrameForCurrentPose = null
                        lastScore = 0f
                    }
                }
            }

            if (currentPoseIndex >= poseLabels.size) return@forEach
        }

        // 리스트를 뒤집어서 반환 (address부터 finish 순서로)
        return bitmapAndKeyPoint.asReversed()
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