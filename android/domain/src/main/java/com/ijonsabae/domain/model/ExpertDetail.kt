package com.ijonsabae.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ExpertDetail(
    val imageUrl : String,
    val name : String,
    val expYears : Int,
    val certificates : List<String>,
    val counselUrl: String,
    val phoneNumber: String
) {
    companion object{
        val EMPTY = ExpertDetail(
            imageUrl = "",
            name = "",
            expYears = -1,
            certificates = listOf(),
            counselUrl = "",
            phoneNumber = ""
        )
    }
}