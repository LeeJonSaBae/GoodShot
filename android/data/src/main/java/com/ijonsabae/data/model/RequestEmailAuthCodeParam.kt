package com.ijonsabae.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RequestEmailAuthCodeParam(private val email: String)
