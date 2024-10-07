package com.ijonsabae.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Token(val accessToken: String, val refreshToken: String, val userId: Long){
    companion object{
        val EMPTY = Token("", "", -1)
    }
}
