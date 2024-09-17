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
import android.util.Log
import com.ijonsabae.presentation.shot.ai.data.KeyPoint
import com.ijonsabae.presentation.shot.ai.data.Person
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil

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

    fun classify(keyPoints: List<KeyPoint>): MutableList<Pair<String, Float>> {
        // Preprocess the pose estimation result to a flat array
        val inputVector = FloatArray(input[1])
        keyPoints.forEachIndexed { index, keyPoint ->
            inputVector[index * 2] = keyPoint.coordinate.x
            inputVector[index * 2 + 1] = keyPoint.coordinate.y
        }

        // Postprocess the model output to human readable class names
        val outputTensor = FloatArray(output[1])
        interpreter.run(arrayOf(inputVector), arrayOf(outputTensor))
        val output = mutableListOf<Pair<String, Float>>()
        outputTensor.forEachIndexed { index, score ->
            output.add(Pair(labels[index], score))
        }
        return output
    }

    fun close() {
        interpreter.close()
    }
}
