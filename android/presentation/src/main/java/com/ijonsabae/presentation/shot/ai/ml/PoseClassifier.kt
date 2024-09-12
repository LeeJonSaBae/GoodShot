package com.ijonsabae.presentation.shot.ai.ml

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

import android.content.Context
import com.ijonsabae.presentation.shot.ai.data.Person
import com.ijonsabae.presentation.shot.ai.vo.PersonWithScore
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil

class PoseClassifier(
    private val interpreter: Interpreter,
    private val labels: List<String>
) {
    private val input = interpreter.getInputTensor(0).shape()
    private val output = interpreter.getOutputTensor(0).shape()

    companion object {
        private const val MODEL_FILENAME = "classifier.tflite"
        private const val LABELS_FILENAME = "labels.txt"
        private const val CPU_NUM_THREADS = 4

        fun create(context: Context): PoseClassifier {
            val options = Interpreter.Options().apply {
                setNumThreads(CPU_NUM_THREADS)
            }
            return PoseClassifier(
                Interpreter(
                    FileUtil.loadMappedFile(
                        context, MODEL_FILENAME
                    ), options
                ),
                FileUtil.loadLabels(context, LABELS_FILENAME)
            )
        }
    }

    // 8개 포즈와의 유사도와 관절 좌표들을 함께 반환하도록 바꾸어야 합니다.
    fun classify(person: Person): PersonWithScore? {
//        // Preprocess the pose estimation result to a flat array
//        val inputVector = FloatArray(input[1])
//        person?.keyPoints?.forEachIndexed { index, keyPoint ->
//            inputVector[index * 3] = keyPoint.coordinate.y
//            inputVector[index * 3 + 1] = keyPoint.coordinate.x
//            inputVector[index * 3 + 2] = keyPoint.score
//        }
//
//        // Postprocess the model output to human readable class names
//        val outputTensor = FloatArray(output[1])
//        interpreter.run(arrayOf(inputVector), arrayOf(outputTensor))
//        val output = mutableListOf<Pair<String, Float>>()
//        outputTensor.forEachIndexed { index, score ->
//            output.add(Pair(labels[index], score))
//        }
//        return output
        return null
    }

    fun close() {
        interpreter.close()
    }
}
