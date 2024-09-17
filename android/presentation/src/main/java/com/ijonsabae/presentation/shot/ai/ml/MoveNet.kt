import android.content.Context
import android.graphics.*
import android.os.SystemClock
import android.util.Log
import com.ijonsabae.presentation.shot.ai.data.BodyPart
import com.ijonsabae.presentation.shot.ai.data.Device
import com.ijonsabae.presentation.shot.ai.data.KeyPoint
import com.ijonsabae.presentation.shot.ai.data.Person
import com.ijonsabae.presentation.shot.ai.data.TorsoAndBodyDistance
import com.ijonsabae.presentation.shot.ai.ml.PoseDetector
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

    override fun estimatePoses(bitmap: Bitmap, isSelf: Boolean): Person {
        val inferenceStartTimeNanos = SystemClock.elapsedRealtimeNanos()

        // 원본 Bitmap 크기 출력
        Log.d("BitmapSize", "Original Bitmap: width = ${bitmap.width}, height = ${bitmap.height}")

        // 세로 길이를 기준으로 가로에 패딩을 추가해 1:1 비율로 만듭니다.
        val targetSize = maxOf(bitmap.width, bitmap.height)

        // 가로 패딩 계산 (세로 길이에 맞춰 가로에 패딩을 추가)
        val widthPadding = maxOf(0, bitmap.height - bitmap.width)

        // 새로운 비트맵을 생성하고 패딩을 추가
        val paddedBitmap = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(paddedBitmap)
        val paint = Paint()
        paint.color = Color.BLACK // 패딩 색상 설정 (필요에 따라 조정)

        canvas.drawRect(0f, 0f, targetSize.toFloat(), targetSize.toFloat(), paint)
        canvas.drawBitmap(
            bitmap,
            (widthPadding / 2).toFloat(), // 가로 중앙에 배치
            0f, // 세로는 그대로
            null
        )
        // 패딩된 비트맵을 imageQueue에 추가
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
                (left * bitmap.width),
                (top * bitmap.height),
                (right * bitmap.width),
                (bottom * bitmap.height)
            )
            val detectBitmap = Bitmap.createBitmap(
                rect.width().toInt(),
                rect.height().toInt(),
                Bitmap.Config.ARGB_8888
            )

            Canvas(detectBitmap).drawBitmap(
                bitmap,
                -rect.left,
                -rect.top,
                null
            )
            val inputTensor = processInputImage(detectBitmap, inputWidth, inputHeight)
            val outputTensor = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32)
            val widthRatio = detectBitmap.width.toFloat() / inputWidth
            val heightRatio = detectBitmap.height.toFloat() / inputHeight

            val positions = mutableListOf<Float>()

            inputTensor?.let { input ->

                interpreter.run(input.buffer, outputTensor.buffer.rewind())
                val output = outputTensor.floatArray
                for (idx in 0 until numKeyPoints) {
                    val x = output[idx * 3 + 1] * inputWidth * widthRatio
                    val y = output[idx * 3 + 0] * inputHeight * heightRatio

                    positions.add(x)
                    positions.add(y)
                    val score = output[idx * 3 + 2]
                    keyPoints.add(
                        KeyPoint(
                            BodyPart.fromInt(idx),
                            PointF(
                                x,
                                y
                            ),
                            score
                        )
                    )
                    totalScore += score
                }
            }
            val matrix = Matrix()
            val points = positions.toFloatArray()

            matrix.postTranslate(rect.left, rect.top)
            matrix.mapPoints(points)
            keyPoints.forEachIndexed { index, keyPoint ->
                keyPoint.coordinate =
                    PointF(
                        points[index * 2],
                        points[index * 2 + 1]
                    )
            }
            // new crop region
            cropRegion = determineRectF(keyPoints, bitmap.width, bitmap.height)
        }
        lastInferenceTimeNanos =
            SystemClock.elapsedRealtimeNanos() - inferenceStartTimeNanos

        // 큐에 넣기 위한 y축 기준 반전
        var adjustedKeyPoints = keyPoints.map { keyPoint ->

            val newCoordinate = PointF(
                keyPoint.coordinate.x / bitmap.width,
                keyPoint.coordinate.y / bitmap.height
            )
            keyPoint.copy(coordinate = newCoordinate)
        }

        if(isSelf){
            adjustedKeyPoints = swapLeftRight(adjustedKeyPoints)
        }

        // 패딩된 관절을 imageQueue에 추가합니다.
        if (jointQueue.size < QUEUE_SIZE) {
            // 큐의 길이가 QUEUE_SIZE 미만이면 큐에 비트맵 추가
            jointQueue.add(adjustedKeyPoints)
        } else {
            // 큐의 길이가 QUEUE_SIZE에 도달하면 맨 앞의 비트맵을 제거하고 새 비트맵을 추가
            jointQueue.poll() // 큐의 맨 앞 요소 제거
            jointQueue.add(adjustedKeyPoints) // 큐의 맨 뒤에 새 비트맵 추가
        }


        return Person(keyPoints = adjustedKeyPoints, score = totalScore / numKeyPoints)
    }

    private fun swapLeftRight(keyPoints: List<KeyPoint>): List<KeyPoint> {
        val swappedKeyPoints = MutableList(keyPoints.size) { KeyPoint(BodyPart.NOSE, PointF(), 0f) }

        // NOSE는 그대로 유지
        swappedKeyPoints[BodyPart.NOSE.position] = keyPoints[BodyPart.NOSE.position]

        // 왼쪽과 오른쪽을 서로 바꿔서 할당
        swappedKeyPoints[BodyPart.LEFT_EYE.position] = keyPoints[BodyPart.RIGHT_EYE.position]
        swappedKeyPoints[BodyPart.RIGHT_EYE.position] = keyPoints[BodyPart.LEFT_EYE.position]
        swappedKeyPoints[BodyPart.LEFT_EAR.position] = keyPoints[BodyPart.RIGHT_EAR.position]
        swappedKeyPoints[BodyPart.RIGHT_EAR.position] = keyPoints[BodyPart.LEFT_EAR.position]
        swappedKeyPoints[BodyPart.LEFT_SHOULDER.position] = keyPoints[BodyPart.RIGHT_SHOULDER.position]
        swappedKeyPoints[BodyPart.RIGHT_SHOULDER.position] = keyPoints[BodyPart.LEFT_SHOULDER.position]
        swappedKeyPoints[BodyPart.LEFT_ELBOW.position] = keyPoints[BodyPart.RIGHT_ELBOW.position]
        swappedKeyPoints[BodyPart.RIGHT_ELBOW.position] = keyPoints[BodyPart.LEFT_ELBOW.position]
        swappedKeyPoints[BodyPart.LEFT_WRIST.position] = keyPoints[BodyPart.RIGHT_WRIST.position]
        swappedKeyPoints[BodyPart.RIGHT_WRIST.position] = keyPoints[BodyPart.LEFT_WRIST.position]
        swappedKeyPoints[BodyPart.LEFT_HIP.position] = keyPoints[BodyPart.RIGHT_HIP.position]
        swappedKeyPoints[BodyPart.RIGHT_HIP.position] = keyPoints[BodyPart.LEFT_HIP.position]
        swappedKeyPoints[BodyPart.LEFT_KNEE.position] = keyPoints[BodyPart.RIGHT_KNEE.position]
        swappedKeyPoints[BodyPart.RIGHT_KNEE.position] = keyPoints[BodyPart.LEFT_KNEE.position]
        swappedKeyPoints[BodyPart.LEFT_ANKLE.position] = keyPoints[BodyPart.RIGHT_ANKLE.position]
        swappedKeyPoints[BodyPart.RIGHT_ANKLE.position] = keyPoints[BodyPart.LEFT_ANKLE.position]

        return swappedKeyPoints
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

