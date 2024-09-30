package com.ijonsabae.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GenerateTemporaryPassWordParam(
    private val name: String,
    private val email: String
)
