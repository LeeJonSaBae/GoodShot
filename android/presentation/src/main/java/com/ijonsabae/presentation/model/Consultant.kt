package com.ijonsabae.presentation.model

import android.os.Parcelable
import com.ijonsabae.domain.model.Consultant
import kotlinx.parcelize.Parcelize

@Parcelize
data class Consultant(
    val name: String,
    val profileImage: String,
    val career: Int,
    val course: String,
    val expertise: String,
    val certification: List<String>,
    val topic: List<String>
):Parcelable


fun convertConsultant(consultant: Consultant): com.ijonsabae.presentation.model.Consultant {
    return com.ijonsabae.presentation.model.Consultant(
        name = consultant.name,
        profileImage = consultant.profileImage,
        career = consultant.career,
        course = consultant.course,
        expertise = consultant.expertise,
        certification = consultant.certification,
        topic = consultant.topic
    )
}