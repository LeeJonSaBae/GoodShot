package com.ijonsabae.data.model

import kotlinx.serialization.Serializable

@Serializable
class TimeStampResponse<T> (
    val errorCode: String,
    val message: String,
    val timestamp: String,
)