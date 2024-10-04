package com.ijonsabae.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Expert(
    val id : Int,
    val imageUrl : String,
    val name : String,
    val expYears : Int,
    val field : String
)
