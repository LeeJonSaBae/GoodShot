package com.ijonsabae.presentation.shot.ai.ml

import android.content.Context
import android.graphics.PointF
import android.util.Log
import com.ijonsabae.presentation.shot.ai.data.BodyPart
import com.ijonsabae.presentation.shot.ai.data.KeyPoint
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil

/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

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


class PoseClassifier(
    private val interpreter: Interpreter,
    private val labels: List<String>
) {
    private val input = interpreter.getInputTensor(0).shape()
    private val output = interpreter.getOutputTensor(0).shape()

    companion object {
        private const val CPU_NUM_THREADS = 4

        fun create(
            context: Context,
            modelFileName: String,
            labelsFileName: String
        ): PoseClassifier {
            val options = Interpreter.Options().apply {
                numThreads = CPU_NUM_THREADS
            }
            return PoseClassifier(
                Interpreter(
                    FileUtil.loadMappedFile(
                        context, modelFileName
                    ), options
                ),
                FileUtil.loadLabels(context, labelsFileName)
            )
        }
    }
    fun classify(keyPoints: List<KeyPoint>): Pair<String, Float> {
        // 코의 위치를 기준으로 정규화
        val noseKeyPoint = keyPoints.find { it.bodyPart == BodyPart.NOSE }
        val normalizedKeyPoints = noseKeyPoint?.let { nose ->
            val referenceX = 0.5572794732116567f
            val referenceY = 0.1998999040023779f

            val offsetX = referenceX - nose.coordinate.x
            val offsetY = referenceY - nose.coordinate.y

            keyPoints.map { keyPoint ->
                KeyPoint(
                    keyPoint.bodyPart,
                    PointF(
                        (keyPoint.coordinate.x + offsetX).coerceIn(0f, 1f),
                        (keyPoint.coordinate.y + offsetY).coerceIn(0f, 1f)
                    ),
                    keyPoint.score
                )
            }
        } ?: keyPoints

        // Preprocess the pose estimation result to a flat array
        val inputVector = FloatArray(input[1])
        normalizedKeyPoints.forEachIndexed { index, keyPoint ->
            inputVector[index] = keyPoint.coordinate.x
            inputVector[index + normalizedKeyPoints.size] = keyPoint.coordinate.y
        }

        // Postprocess the model output to human readable class names
        val outputTensor = FloatArray(output[1])
        interpreter.run(arrayOf(inputVector), arrayOf(outputTensor))

        var maxScore = 0f
        var predictedPoseIndex = 0
        var predList = mutableListOf<String>()
        outputTensor.forEachIndexed { index, score ->
            if (score > maxScore) {
                maxScore = score
                predictedPoseIndex = index
            }
            predList.add("${labels[index]} : ${String.format("%.1f", score * 100)}%")
        }

        Log.d("분류기", "${predList.toString()}: ")
        return Pair(labels[predictedPoseIndex], maxScore)
    }
    fun close() {
        interpreter.close()
    }
}
