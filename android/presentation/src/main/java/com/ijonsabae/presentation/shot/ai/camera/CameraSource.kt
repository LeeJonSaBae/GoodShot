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


import com.ijonsabae.presentation.shot.ai.ml.PoseClassifier
import com.ijonsabae.presentation.shot.ai.ml.PoseDetector
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.hardware.camera2.CameraDevice
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.SurfaceView
import com.ijonsabae.presentation.shot.SwingViewModel
import com.ijonsabae.presentation.shot.ai.data.Device
import com.ijonsabae.presentation.shot.ai.data.Person
import com.ijonsabae.presentation.shot.ai.utils.VisualizationUtils
import java.util.Timer
import java.util.TimerTask
import kotlin.math.max


class CameraSource(
    private val context: Context,
    private val swingViewModel: SwingViewModel,
    private val surfaceView: SurfaceView
) {
    private var listener: CameraSourceListener? = null
    private var lock = Any()
    private var classifier4: PoseClassifier? = null
    private var classifier8: PoseClassifier? = null
    private var detector: PoseDetector? = null
    private var isTrackerEnabled = false

    init {
        // Detector
        val poseDetector = MoveNet.create(context, Device.CPU, ModelType.Lightning)
        setDetector(poseDetector)

        // Classifier
        val classifier4 = PoseClassifier.create(context, MODEL_FILENAME_4, LABELS_FILENAME_4)
        val classifier8 = PoseClassifier.create(context, MODEL_FILENAME_8, LABELS_FILENAME_8)
        setClassifier(classifier4, classifier8)

        // CameraSourceListener
        listener = CameraSourceListenerImpl(swingViewModel, poseDetector, classifier4, classifier8)
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
    }

    /** Frame count that have been processed so far in an one second interval to calculate FPS. */
    private var fpsTimer: Timer? = null
    private var frameProcessedInOneSecondInterval = 0
    private var framesPerSecond = 1

    private var frameCount = 0
    private val TARGET_FPS = 24

//    /** Readers used as buffers for camera still shots */
//    private var imageReader: ImageReader? = null

    /*    */
    /** The [CameraDevice] that will be opened in this fragment *//*
    private var camera: CameraDevice? = null*/

//    /** Internal reference to the ongoing [CameraCaptureSession] configured with our parameters */
//    private var session: CameraCaptureSession? = null

    /** [HandlerThread] where all buffer reading operations run */
    private var imageReaderThread: HandlerThread? = null

    /** [Handler] corresponding to [imageReaderThread] */
    private var imageReaderHandler: Handler? = null

    fun getRotateBitmap(bitmap: Bitmap, width: Int, height: Int, self: Boolean): Bitmap {
        val rotateMatrix = Matrix()

        if (self) {
            rotateMatrix.postRotate(270.0f)
            rotateMatrix.postScale(-1F, 1F)
        } else {
            rotateMatrix.postRotate(90.0f)
            rotateMatrix.postScale(1F, 1F)
        }

        val rotateBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, rotateMatrix, false)
//        return rotateBitmap
        // Bitmap과 View의 비율 계산
        val bitmapRatio = rotateBitmap.width.toFloat() / rotateBitmap.height.toFloat()
        val viewRatio = width.toFloat() / height.toFloat()

        var croppedBitmap = rotateBitmap

        // Bitmap이 View보다 가로로 길 때 (비율에 맞게 가로를 자름)
        if (bitmapRatio > viewRatio) {
            val newWidth = (rotateBitmap.height * viewRatio).toInt()
            val cropStartX = (rotateBitmap.width - newWidth) / 2
            // 가로를 자르고 중앙에 맞추기
            croppedBitmap =
                Bitmap.createBitmap(rotateBitmap, cropStartX, 0, newWidth, rotateBitmap.height)
        }
        // Bitmap이 View보다 세로로 길 때 (비율에 맞게 세로를 자름)
        else if (bitmapRatio < viewRatio) {
            val newHeight = (rotateBitmap.width / viewRatio).toInt()
            val cropStartY = (rotateBitmap.height - newHeight) / 2
            // 세로를 자르고 중앙에 맞추기
            croppedBitmap =
                Bitmap.createBitmap(rotateBitmap, 0, cropStartY, rotateBitmap.width, newHeight)
        }

        // 크기를 View의 크기에 맞게 확장
        return Bitmap.createScaledBitmap(croppedBitmap, width, height, false)
    }

    fun setDetector(detector: PoseDetector) {
        synchronized(lock) {
            if (this.detector != null) {
                this.detector?.close()
                this.detector = null
            }
            this.detector = detector
        }
    }

    fun setClassifier(classifier4: PoseClassifier, classifier8: PoseClassifier) {
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
        fpsTimer = Timer()

        fpsTimer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    framesPerSecond = frameProcessedInOneSecondInterval
                    frameProcessedInOneSecondInterval = 0
                }
            },
            0,
            1000
        )
    }

    fun pause() {
        stopImageReaderThread()
        fpsTimer?.cancel()
        fpsTimer = null
        frameProcessedInOneSecondInterval = 0
        framesPerSecond = 0
    }

    fun destroy(){
        detector?.close()
        detector = null
        classifier4?.close()
        classifier4 = null
        classifier8?.close()
        classifier8 = null
    }

    fun processImage(bitmap: Bitmap) {
        frameCount++

        // framesPerSecond가 0이거나 TARGET_FPS보다 작으면 모든 프레임을 처리합니다.
        val shouldProcessFrame =
            framesPerSecond <= TARGET_FPS || frameCount % max(1, framesPerSecond / TARGET_FPS) == 0

        if (shouldProcessFrame) {
            var poseResult: Person?

            synchronized(lock) {
                // estimatePoses 에서 각 관절의 이름과 좌표가 반환됨
                poseResult = detector?.estimatePoses(bitmap)
            }

            frameProcessedInOneSecondInterval++
            poseResult?.let {
                listener?.onDetectedInfo(it)
                visualize(it, bitmap)
            }
        } else {
            Log.d(TAG, "processImage: 처리 안함")
        }
    }


    private fun visualize(person: Person, bitmap: Bitmap) {
        val personList = if (person.score > MIN_CONFIDENCE) listOf(person) else listOf()
        Log.d(TAG, "visualize: drawPerson: $personList")
        val outputBitmap = VisualizationUtils.drawBodyKeypoints(
            bitmap,
            personList,
            isTrackerEnabled
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
}