package com.ijonsabae.domain.model

import java.io.Serializable

@kotlinx.serialization.Serializable
data class ProfileImage(
    val uri: String,
    val name: String,
    val mimeType: String
) : Serializable