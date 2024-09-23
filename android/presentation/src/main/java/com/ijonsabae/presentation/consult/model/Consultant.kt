package com.ijonsabae.presentation.consult.model

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
