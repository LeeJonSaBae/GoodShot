package com.ijonsabae.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CommonResponse<T>(
    val code: String,
    val message: String,
    val data: T,
)

fun <T> CommonResponse<T>.getOrThrow(): T {
    when (code) {
        "200" -> {
            return data
        }

        "201" -> {
            return data
        }

        else -> {

        }
    }
    return data
}