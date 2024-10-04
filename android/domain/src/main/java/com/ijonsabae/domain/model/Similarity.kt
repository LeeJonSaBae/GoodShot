package com.ijonsabae.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Similarity(
    val address: Double,
    val toeUp: Double,
    val midBackSwing: Double,
    val top: Double,
    val midDownSwing: Double,
    val impact: Double,
    val midFollowThrough: Double,
    val finish: Double
)
