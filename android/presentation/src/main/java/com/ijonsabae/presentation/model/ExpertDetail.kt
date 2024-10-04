package com.ijonsabae.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExpertDetail(
    val imageUrl : String,
    val name : String,
    val expYears : Int,
    val certificates : List<String>,
    val counselUrl: String,
    val phoneNumber: String
): Parcelable {
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

fun convertExpertDetail(expertDetail: com.ijonsabae.domain.model.ExpertDetail): ExpertDetail{
    return ExpertDetail(
        name = expertDetail.name,
        phoneNumber = expertDetail.phoneNumber,
        expYears = expertDetail.expYears,
        imageUrl = expertDetail.imageUrl,
        certificates = expertDetail.certificates,
        counselUrl = expertDetail.counselUrl
    )
}