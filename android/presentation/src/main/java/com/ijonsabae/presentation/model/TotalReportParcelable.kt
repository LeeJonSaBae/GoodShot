package com.ijonsabae.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TotalReportParcelable(
    val content: String,
    val name: String,
    val problems: List<String>,
    val score: Double,
    val similarity: List<Double>
): Parcelable