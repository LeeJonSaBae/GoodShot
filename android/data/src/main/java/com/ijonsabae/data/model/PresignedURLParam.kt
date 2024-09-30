package com.ijonsabae.data.model

import kotlinx.serialization.Serializable

private const val TAG = "굿샷_ProfileParam"

@Serializable
data class PresignedURLParam(
    val imageExtension: String
)