import android.content.Context
import android.graphics.*
import android.os.SystemClock
import android.util.Log
import com.ijonsabae.presentation.shot.ai.data.BodyPart
import com.ijonsabae.presentation.shot.ai.data.Device
import com.ijonsabae.presentation.shot.ai.data.KeyPoint
import com.ijonsabae.presentation.shot.ai.data.Person
import com.ijonsabae.presentation.shot.ai.data.TorsoAndBodyDistance
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.util.LinkedList
import java.util.Queue
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class TimestampedData<T>(val data: T, val timestamp: Long)
enum class ModelType {
    Lightning,
    Thunder
}

// 전역 변수로 imageQueue 생성
val imageQueue: Queue<TimestampedData<Bitmap>> = LinkedList()
val jointQueue: Queue<List<KeyPoint>> = LinkedList()

class MoveNet(private val interpreter: Interpreter, private var gpuDelegate: GpuDelegate?) :

    PoseDetector {
    companion object {
        private const val MIN_CROP_KEYPOINT_SCORE = .2f
        private const val CPU_NUM_THREADS = 4
        private const val QUEUE_SIZE = 24 * 3 // 24fps * 3초 = 72

        // Parameters that control how large crop region should be expanded from previous frames'
        // body keypoints.
        private const val TORSO_EXPANSION_RATIO = 1.9f
        private const val BODY_EXPANSION_RATIO = 1.2f

        // TFLite file names.
        private const val LIGHTNING_FILENAME = "movenet_lightning.tflite"
        private const val THUNDER_FILENAME = "movenet_thunder.tflite"

        fun create(context: Context, device: Device, modelType: ModelType): MoveNet {
            val options = Interpreter.Options()
            var gpuDelegate: GpuDelegate? = null
            options.setNumThreads(CPU_NUM_THREADS)
            when (device) {
                Device.CPU -> {
                }

                Device.GPU -> {
                    gpuDelegate = GpuDelegate()
                    options.addDelegate(gpuDelegate)
                }

                Device.NNAPI -> options.setUseNNAPI(true)
            }

            val moveNet = MoveNet(
                Interpreter(
                    FileUtil.loadMappedFile(
                        context,
                        if (modelType == ModelType.Lightning) LIGHTNING_FILENAME
                        else THUNDER_FILENAME
                    ), options
                ),
                gpuDelegate
            )


            return moveNet
        }

        // default to lightning.
        fun create(context: Context, device: Device): MoveNet =
            create(context, device, ModelType.Lightning)
    }

    private var cropRegion: RectF? = null
    private var lastInferenceTimeNanos: Long = -1
    private val inputWidth = interpreter.getInputTensor(0).shape()[1]
    private val inputHeight = interpreter.getInputTensor(0).shape()[2]
    private var outputShape: IntArray = interpreter.getOutputTensor(0).shape()

    fun getQueuedData(): Pair<List<Pair<Bitmap, Long>>, List<List<KeyPoint>>> {
        val images = imageQueue.toList().map { Pair(it.data, it.timestamp) }
        val joints = jointQueue.toList()
        return Pair(images, joints)
    }

    override fun estimatePoses(bitmap: Bitmap): Person {
        val inferenceStartTimeNanos = SystemClock.elapsedRealtimeNanos()

        Log.d("BitmapSize", "Original Bitmap: width = ${bitmap.width}, height = ${bitmap.height}")

        val matrix = Matrix().apply {
            preScale(-1f, 1f)
        }
        val flippedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        val targetSize = maxOf(flippedBitmap.width, flippedBitmap.height)
        val widthPadding = maxOf(0, flippedBitmap.height - flippedBitmap.width)

        val paddedBitmap = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(paddedBitmap)
        val paint = Paint().apply { color = Color.BLACK }

        canvas.drawRect(0f, 0f, targetSize.toFloat(), targetSize.toFloat(), paint)
        canvas.drawBitmap(flippedBitmap, (widthPadding / 2).toFloat(), 0f, null)

        val currentTime = System.currentTimeMillis()
        if (imageQueue.size >= QUEUE_SIZE) {
            imageQueue.poll()
        }
        imageQueue.offer(TimestampedData(paddedBitmap, currentTime))

        if (cropRegion == null) {
            cropRegion = initRectF(bitmap.width, bitmap.height)
        }

        var totalScore = 0f
        val numKeyPoints = outputShape[2]
        val keyPoints = mutableListOf<KeyPoint>()

        cropRegion?.run {
            val rect = RectF(
                (left * bitmap.width).coerceAtLeast(0f),
                (top * bitmap.height).coerceAtLeast(0f),
                (right * bitmap.width).coerceAtMost(bitmap.width.toFloat()),
                (bottom * bitmap.height).coerceAtMost(bitmap.height.toFloat())
            )

            // 최소 크기 설정
            val minSize = 1
            val width = maxOf(rect.width().toInt(), minSize)
            val height = maxOf(rect.height().toInt(), minSize)

            val detectBitmap = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888
            )

            Canvas(detectBitmap).drawBitmap(flippedBitmap, -rect.left, -rect.top, null)
            val inputTensor = processInputImage(detectBitmap, inputWidth, inputHeight)
            val outputTensor = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32)

            inputTensor?.let { input ->
                interpreter.run(input.buffer, outputTensor.buffer.rewind())
                val output = outputTensor.floatArray
                for (idx in 0 until numKeyPoints) {
                    val x = output[idx * 3 + 1]
                    val y = output[idx * 3 + 0]
                    val score = output[idx * 3 + 2]

                    keyPoints.add(
                        KeyPoint(
                            BodyPart.fromInt(idx),
                            PointF(x, y),
                            score
                        )
                    )
                    totalScore += score
                }
            }

            // new crop region
            cropRegion = determineRectF(keyPoints, bitmap.width, bitmap.height)
        }
        lastInferenceTimeNanos = SystemClock.elapsedRealtimeNanos() - inferenceStartTimeNanos

        // 정규화된 좌표를 그대로 사용
        val flippedKeypoints = keyPoints.map { keyPoint ->
            val newCoordinate = PointF(
                1 - keyPoint.coordinate.x,  // x 좌표만 반전
                keyPoint.coordinate.y
            )
            keyPoint.copy(coordinate = newCoordinate)
        }

        if (jointQueue.size < 70) {
            jointQueue.add(flippedKeypoints)
        } else {
            jointQueue.poll()
            jointQueue.add(flippedKeypoints)
        }

        flippedBitmap.recycle()

        return Person(keyPoints = flippedKeypoints, score = totalScore / numKeyPoints)
    }

    override fun lastInferenceTimeNanos(): Long = lastInferenceTimeNanos

    override fun close() {
        gpuDelegate?.close()
        interpreter.close()
        cropRegion = null
    }

    /**
     * Prepare input image for detection
     */
    private fun processInputImage(bitmap: Bitmap, inputWidth: Int, inputHeight: Int): TensorImage? {
        val width: Int = bitmap.width
        val height: Int = bitmap.height

        val size = if (height > width) width else height
        val imageProcessor = ImageProcessor.Builder().apply {
            add(ResizeWithCropOrPadOp(size, size))
            add(ResizeOp(inputWidth, inputHeight, ResizeOp.ResizeMethod.BILINEAR))
        }.build()
        val tensorImage = TensorImage(DataType.UINT8)
        tensorImage.load(bitmap)

        return imageProcessor.process(tensorImage)
    }

    /**
     * Defines the default crop region.
     * The function provides the initial crop region (pads the full image from both
     * sides to make it a square image) when the algorithm cannot reliably determine
     * the crop region from the previous frame.
     */
    private fun initRectF(imageWidth: Int, imageHeight: Int): RectF {
        val xMin: Float
        val yMin: Float
        val width: Float
        val height: Float
        if (imageWidth > imageHeight) {
            width = 1f
            height = imageWidth.toFloat() / imageHeight
            xMin = 0f
            yMin = (imageHeight / 2f - imageWidth / 2f) / imageHeight
        } else {
            height = 1f
            width = imageHeight.toFloat() / imageWidth
            yMin = 0f
            xMin = (imageWidth / 2f - imageHeight / 2) / imageWidth
        }
        return RectF(
            xMin,
            yMin,
            xMin + width,
            yMin + height
        )
    }


    private fun torsoVisible(keyPoints: List<KeyPoint>): Boolean {
        return ((keyPoints[BodyPart.LEFT_HIP.position].score > MIN_CROP_KEYPOINT_SCORE).or(
            keyPoints[BodyPart.RIGHT_HIP.position].score > MIN_CROP_KEYPOINT_SCORE
        )).and(
            (keyPoints[BodyPart.LEFT_SHOULDER.position].score > MIN_CROP_KEYPOINT_SCORE).or(
                keyPoints[BodyPart.RIGHT_SHOULDER.position].score > MIN_CROP_KEYPOINT_SCORE
            )
        )
    }

    private fun determineRectF(
        keyPoints: List<KeyPoint>,
        imageWidth: Int,
        imageHeight: Int
    ): RectF {
        val targetKeyPoints = mutableListOf<KeyPoint>()
        keyPoints.forEach {
            targetKeyPoints.add(
                KeyPoint(
                    it.bodyPart,
                    PointF(
                        it.coordinate.x,
                        it.coordinate.y
                    ),
                    it.score
                )
            )
        }
        if (torsoVisible(keyPoints)) {
            val centerX =
                (targetKeyPoints[BodyPart.LEFT_HIP.position].coordinate.x +
                        targetKeyPoints[BodyPart.RIGHT_HIP.position].coordinate.x) / 2f
            val centerY =
                (targetKeyPoints[BodyPart.LEFT_HIP.position].coordinate.y +
                        targetKeyPoints[BodyPart.RIGHT_HIP.position].coordinate.y) / 2f

            val torsoAndBodyDistances =
                determineTorsoAndBodyDistances(keyPoints, targetKeyPoints, centerX, centerY)

            val list = listOf(
                torsoAndBodyDistances.maxTorsoXDistance * TORSO_EXPANSION_RATIO,
                torsoAndBodyDistances.maxTorsoYDistance * TORSO_EXPANSION_RATIO,
                torsoAndBodyDistances.maxBodyXDistance * BODY_EXPANSION_RATIO,
                torsoAndBodyDistances.maxBodyYDistance * BODY_EXPANSION_RATIO
            )

            var cropLengthHalf = list.maxOrNull() ?: 0f
            val tmp = listOf(centerX, imageWidth - centerX, centerY, imageHeight - centerY)
            cropLengthHalf = min(cropLengthHalf, tmp.maxOrNull() ?: 0f)
            val cropCorner = Pair(centerY - cropLengthHalf, centerX - cropLengthHalf)

            return if (cropLengthHalf > max(imageWidth, imageHeight) / 2f) {
                initRectF(imageWidth, imageHeight)
            } else {
                val cropLength = cropLengthHalf * 2
                RectF(
                    cropCorner.second / imageWidth,
                    cropCorner.first / imageHeight,
                    (cropCorner.second + cropLength) / imageWidth,
                    (cropCorner.first + cropLength) / imageHeight,
                )
            }
        } else {
            return initRectF(imageWidth, imageHeight)
        }
    }

    private fun determineTorsoAndBodyDistances(
        keyPoints: List<KeyPoint>,
        targetKeyPoints: List<KeyPoint>,
        centerX: Float,
        centerY: Float
    ): TorsoAndBodyDistance {
        val torsoJoints = listOf(
            BodyPart.LEFT_SHOULDER.position,
            BodyPart.RIGHT_SHOULDER.position,
            BodyPart.LEFT_HIP.position,
            BodyPart.RIGHT_HIP.position
        )

        var maxTorsoYRange = 0f
        var maxTorsoXRange = 0f
        torsoJoints.forEach { joint ->
            val distY = abs(centerY - targetKeyPoints[joint].coordinate.y)
            val distX = abs(centerX - targetKeyPoints[joint].coordinate.x)
            if (distY > maxTorsoYRange) maxTorsoYRange = distY
            if (distX > maxTorsoXRange) maxTorsoXRange = distX
        }

        var maxBodyYRange = 0f
        var maxBodyXRange = 0f
        for (joint in keyPoints.indices) {
            if (keyPoints[joint].score < MIN_CROP_KEYPOINT_SCORE) continue
            val distY = abs(centerY - keyPoints[joint].coordinate.y)
            val distX = abs(centerX - keyPoints[joint].coordinate.x)

            if (distY > maxBodyYRange) maxBodyYRange = distY
            if (distX > maxBodyXRange) maxBodyXRange = distX
        }
        return TorsoAndBodyDistance(
            maxTorsoYRange,
            maxTorsoXRange,
            maxBodyYRange,
            maxBodyXRange
        )
    }
}
