package com.ijonsabae.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordParam(
    val oldPassword: String,
    val newPassword: String,
)
