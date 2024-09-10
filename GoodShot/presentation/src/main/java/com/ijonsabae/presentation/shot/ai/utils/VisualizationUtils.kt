package com.ijonsabae.presentation.shot.ai.utils/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

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


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.ijonsabae.presentation.shot.ai.data.BodyPart
import com.ijonsabae.presentation.shot.ai.data.Person

object VisualizationUtils {
    /** Radius of circle used to draw keypoints.  */
    private const val CIRCLE_RADIUS = 6f

    /** Width of line used to connected two keypoints.  */
    private const val LINE_WIDTH = 4f

    /** The text size of the person id that will be displayed when the tracker is available.  */
    private const val PERSON_ID_TEXT_SIZE = 30f

    /** Distance from person id to the nose keypoint.  */
    private const val PERSON_ID_MARGIN = 6f

    /** Pair of keypoints to draw lines between.  */
    private val bodyJoints = listOf(
        Pair(BodyPart.NOSE, BodyPart.LEFT_EYE),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_EYE),
        Pair(BodyPart.LEFT_EYE, BodyPart.LEFT_EAR),
        Pair(BodyPart.RIGHT_EYE, BodyPart.RIGHT_EAR),
        Pair(BodyPart.NOSE, BodyPart.LEFT_SHOULDER),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_ELBOW),
        Pair(BodyPart.LEFT_ELBOW, BodyPart.LEFT_WRIST),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW),
        Pair(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE),
        Pair(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE),
        Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE),
        Pair(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE)
    )

    // Draw line and point indicate body pose

    fun drawBodyKeypoints(
        input: Bitmap,
        persons: List<Person>,
        isTrackerEnabled: Boolean = false
    ): Bitmap {
        val width = input.width
        val paintCircle = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.RED
            style = Paint.Style.FILL
        }
        val paintLine = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.RED
            style = Paint.Style.STROKE
        }
        val paintCircleOutline = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.GREEN
            style = Paint.Style.STROKE
        }

        val output = input.copy(Bitmap.Config.ARGB_8888, true)
        val originalSizeCanvas = Canvas(output)

        persons.forEach { person ->
            // 눈, 코, 귀에 대한 선을 제외하고 나머지 부위만 그리기
            bodyJoints.forEach {
                if (it.first !in listOf(BodyPart.NOSE, BodyPart.LEFT_EYE, BodyPart.RIGHT_EYE, BodyPart.LEFT_EAR, BodyPart.RIGHT_EAR) &&
                    it.second !in listOf(BodyPart.NOSE, BodyPart.LEFT_EYE, BodyPart.RIGHT_EYE, BodyPart.LEFT_EAR, BodyPart.RIGHT_EAR)
                ) {
                    val pointA = person.keyPoints[it.first.position].coordinate
                    val pointB = person.keyPoints[it.second.position].coordinate
                    originalSizeCanvas.drawLine(
                        width - pointA.x, pointA.y,
                        width - pointB.x, pointB.y,
                        paintLine
                    )
                }
            }

            // 눈, 코, 귀를 제외한 점만 그리기
            person.keyPoints.forEach { point ->
                if (point.bodyPart !in listOf(BodyPart.NOSE, BodyPart.LEFT_EYE, BodyPart.RIGHT_EYE, BodyPart.LEFT_EAR, BodyPart.RIGHT_EAR)) {
                    originalSizeCanvas.drawCircle(
                        width - point.coordinate.x,
                        point.coordinate.y,
                        CIRCLE_RADIUS,
                        paintCircle
                    )
                }
            }

            // 코와 두 귀를 걸치는 외접원을 그리기
            val nose = person.keyPoints[BodyPart.NOSE.position].coordinate
            val leftEar = person.keyPoints[BodyPart.LEFT_EAR.position].coordinate
            val rightEar = person.keyPoints[BodyPart.RIGHT_EAR.position].coordinate

            // 외접원의 중심과 반지름 계산 (X 좌표 반전)
            val centerX = width - ((nose.x + leftEar.x + rightEar.x) / 3)
            val centerY = (nose.y + leftEar.y + rightEar.y) / 3
            val radius = maxOf(
                distance(width - nose.x, nose.y, centerX, centerY),
                distance(width - leftEar.x, leftEar.y, centerX, centerY),
                distance(width - rightEar.x, rightEar.y, centerX, centerY)
            )

            // 외접원 그리기
            originalSizeCanvas.drawCircle(centerX, centerY, radius, paintCircleOutline)
        }
        return output
    }

    // 두 점 사이의 거리를 계산하는 함수 (X 좌표가 이미 반전된 상태로 전달됨)
    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return kotlin.math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
    }
}
