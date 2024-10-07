package com.ijonsabae.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SwingFeedbackCommentParcelable(
    val userID: Long,
    val swingCode: String,
    val poseType: Int,
    val content: String,
    val commentType: Int
): Parcelable
