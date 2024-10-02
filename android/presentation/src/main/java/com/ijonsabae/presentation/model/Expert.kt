package com.ijonsabae.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Expert(
    val id: Int,
    val imageUrl: String,
    val name: String,
    val expYears: Int,
    val field: String
) : Parcelable


fun convertExpert(expert: com.ijonsabae.domain.model.Expert): Expert {
    return Expert(
        id = expert.id,
        name = expert.name,
        imageUrl = expert.imageUrl,
        expYears = expert.expYears,
        field = expert.field,
    )
}