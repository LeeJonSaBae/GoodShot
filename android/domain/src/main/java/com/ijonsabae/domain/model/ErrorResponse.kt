package com.ijonsabae.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse<T>(
    val errorCode: Int,
    val message: String,
    val data: T,
)