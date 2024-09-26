package com.ijonsabae.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterParam(val name: String, val email: String, val password: String)
