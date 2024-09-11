package com.ijonsabae.presentation.shot.ai.vo

import com.ijonsabae.presentation.shot.ai.data.KeyPoint

data class FrameData(val timestamp: Long, val keyPoints: List<KeyPoint>)
