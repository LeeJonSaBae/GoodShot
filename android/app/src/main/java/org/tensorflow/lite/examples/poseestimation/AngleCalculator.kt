package org.tensorflow.lite.examples.poseestimation

import android.graphics.PointF
import kotlin.math.atan2

fun calculateAngle(point1: PointF, point2: PointF, point3: PointF): Float {
    // 벡터 계산
    val vector1 = PointF(point1.x - point2.x, point1.y - point2.y)
    val vector2 = PointF(point3.x - point2.x, point3.y - point2.y)

    // 아크탄젠트를 사용하여 각도 계산
    val angle1 = atan2(vector1.y.toDouble(), vector1.x.toDouble())
    val angle2 = atan2(vector2.y.toDouble(), vector2.x.toDouble())

    // 두 각도의 차이 계산
    var angleDiff = Math.toDegrees((angle2 - angle1).toDouble()).toFloat()

    // 각도를 0에서 360도 사이로 조정
    if (angleDiff < 0) {
        angleDiff += 360f
    }

    // 내각 계산 (360도에서 외각을 뺌)
    val innerAngle = 360f - angleDiff

    return if (innerAngle > 180f) {
        360f - innerAngle
    } else {
        innerAngle
    }
}