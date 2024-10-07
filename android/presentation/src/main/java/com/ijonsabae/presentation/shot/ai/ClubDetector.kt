package com.ijonsabae.presentation.shot.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.vision.detector.ObjectDetector

object ClubDetector {
    private const val MODEL_FILE = "club_detect.tflite"
    private const val IMAGE_SIZE = 320
    private const val SCORE_THRESHOLD = 0.5f
    private const val MAX_RESULTS = 10

    private lateinit var objectDetector: ObjectDetector

    fun initialize(context: Context) {
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setScoreThreshold(SCORE_THRESHOLD)
            .setMaxResults(MAX_RESULTS)
            .build()
        objectDetector = ObjectDetector.createFromFileAndOptions(context, MODEL_FILE, options)
    }

    fun detectClub(bitmap: Bitmap): List<DetectionResult> {
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(IMAGE_SIZE, IMAGE_SIZE, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        val results = objectDetector.detect(tensorImage)

        return results.map { detection ->
            val boundingBox = detection.boundingBox
            DetectionResult(
                RectF(
                    boundingBox.left * bitmap.width / IMAGE_SIZE,
                    boundingBox.top * bitmap.height / IMAGE_SIZE,
                    boundingBox.right * bitmap.width / IMAGE_SIZE,
                    boundingBox.bottom * bitmap.height / IMAGE_SIZE
                ),
                detection.categories[0].score
            )
        }
    }

    data class DetectionResult(val boundingBox: RectF, val score: Float)
}