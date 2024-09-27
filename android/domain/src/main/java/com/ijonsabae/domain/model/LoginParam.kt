package com.ijonsabae.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginParam(val email: String, val password: String)
