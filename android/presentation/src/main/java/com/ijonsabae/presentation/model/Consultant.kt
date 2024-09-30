package com.ijonsabae.presentation.model

import android.os.Parcelable
import com.ijonsabae.domain.model.Consultant
import kotlinx.parcelize.Parcelize

@Parcelize
data class Consultant(
    val name: String,
    val profileImage: String,
    val career: Int,
    val pro: String,
    val phoneNumber: String,
    val certification: List<String>,
    val chatUrl: String
):Parcelable


fun convertConsultant(consultant: Consultant): com.ijonsabae.presentation.model.Consultant {
    return com.ijonsabae.presentation.model.Consultant(
        name = consultant.name,
        profileImage = consultant.profileImage,
        career = consultant.career,
        pro = consultant.pro,
        phoneNumber = consultant.phoneNumber,
        certification = consultant.certification,
        chatUrl = consultant.chatUrl
    )
}