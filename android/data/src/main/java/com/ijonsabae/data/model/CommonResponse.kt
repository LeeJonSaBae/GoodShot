package com.ijonsabae.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CommonResponse<T>(
    val code: Int,
    val message: String,
    val data: T
)
