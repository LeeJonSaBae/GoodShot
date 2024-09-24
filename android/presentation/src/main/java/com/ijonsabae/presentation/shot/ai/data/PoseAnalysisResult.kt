package com.ijonsabae.presentation.shot.ai.data

import android.graphics.Bitmap

data class PoseAnalysisResult(val pose : Pose, val bitmap: Bitmap, val feedbacks: List<Feedback>)
