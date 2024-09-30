package com.ijonsabae.presentation.profile

import kotlinx.serialization.Serializable

@Serializable
data class PresignedURLParam(
    val imageExtension: String
)