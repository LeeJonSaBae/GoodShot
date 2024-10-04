package com.ijonsabae.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ExpertList(
    val pageNo: Int,
    val pageSize : Int,
    val hasNext : Boolean,
    val experts : List<Expert>
)
