package com.ijonsabae.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterParam(val name: String, val email: String, val password: String)
