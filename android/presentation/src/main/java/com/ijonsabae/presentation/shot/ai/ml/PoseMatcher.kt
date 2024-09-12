package com.ijonsabae.presentation.shot.ai.ml
import android.content.Context
import com.ijonsabae.presentation.shot.ai.data.KeyPoint
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.io.InputStream

class PoseMatcher(context: Context) {
    private val interpreter: Interpreter
    private val poseNames = arrayOf("Pose1/8", "Pose2/8", "Pose3/8", "Pose4/8", "Pose5/8", "Pose6/8", "Pose7/8", "Pose8/8")

    init {
        val assetManager = context.assets
        val modelFile = assetManager.open("pose_classifier.tflite")
        val modelBuffer = convertStreamToByteBuffer(modelFile)
        interpreter = Interpreter(modelBuffer)
    }

    fun classifyPose(keyPoints: List<KeyPoint>): Pair<String, Float> {
        val inputArray = convertKeyPointsToInputArray(keyPoints)

        // 입력 데이터 준비
        val inputBuffer = ByteBuffer.allocateDirect(4 * inputArray.size)
        inputBuffer.order(ByteOrder.nativeOrder())
        inputBuffer.asFloatBuffer().put(inputArray)

        // 출력 버퍼 준비 (배치 차원 포함)
        val outputBuffer = Array(1) { FloatArray(8) }

        // 모델 추론
        interpreter.run(inputBuffer, outputBuffer)

        // 결과 처리
        val result = outputBuffer[0]
        val maxIndex = result.indices.maxByOrNull { result[it] } ?: -1
        val maxProbability = result[maxIndex]

        return Pair(poseNames[maxIndex], maxProbability)
    }

    private fun convertKeyPointsToInputArray(keyPoints: List<KeyPoint>): FloatArray {
        val inputArray = FloatArray(34)
        keyPoints.forEachIndexed { index, keyPoint ->
            inputArray[index * 2] = keyPoint.coordinate.x
            inputArray[index * 2 + 1] = keyPoint.coordinate.y
        }
        return inputArray
    }

    private fun convertStreamToByteBuffer(inputStream: InputStream): ByteBuffer {
        val byteArray = inputStream.readBytes()
        val byteBuffer = ByteBuffer.allocateDirect(byteArray.size).order(ByteOrder.nativeOrder())
        byteBuffer.put(byteArray)
        byteBuffer.rewind()
        return byteBuffer
    }
}