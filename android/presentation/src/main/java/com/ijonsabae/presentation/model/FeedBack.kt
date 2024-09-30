package com.ijonsabae.presentation.model

import android.os.Parcelable
import com.ijonsabae.domain.model.FeedBack
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedBack(
    val down: Float,
    val tempo: Float,
    val back: Float,
    val feedBackSolution: String,
    val feedBackCheckListTitle: String,
    val feedBackCheckList: List<String>
) : Parcelable

fun convertFeedBack(feedBack: FeedBack): com.ijonsabae.presentation.model.FeedBack {
    return com.ijonsabae.presentation.model.FeedBack(
        down = feedBack.down,
        tempo = feedBack.tempo,
        back = feedBack.back,
        feedBackSolution = feedBack.feedBackSolution,
        feedBackCheckListTitle = feedBack.feedBackCheckListTitle,
        feedBackCheckList = feedBack.feedBackCheckList
    )
}
