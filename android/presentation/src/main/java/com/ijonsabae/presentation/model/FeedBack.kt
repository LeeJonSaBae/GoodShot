package com.ijonsabae.presentation.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedBack(
    val down: String,
    val tempo: String,
    val back: String,
    val feedBackSolution: String,
    val feedBackCheckListTitle: String,
    val feedBackCheckList: List<String>,
    val userSwingImage : Bitmap,
    val expertSwingImageResId : Int
) : Parcelable
