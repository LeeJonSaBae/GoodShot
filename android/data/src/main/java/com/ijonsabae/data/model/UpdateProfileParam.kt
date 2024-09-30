package com.ijonsabae.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileParam(
    val profileUrl: String
)