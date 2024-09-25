package com.ijonsabae.presentation.model

import android.os.Parcelable
import com.ijonsabae.domain.model.FeedBack
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedBack(
    val down: Float,
    val tempo: Float,
    val back: Float,
    val feedBackProblem: String,
    val feedBackSolution: String,
    val feedBackCheckListTitle: String,
    val feedBackCheckList: List<String>
) : Parcelable

fun convertFeedBack(feedBack: FeedBack) : com.ijonsabae.presentation.model.FeedBack{
    return com.ijonsabae.presentation.model.FeedBack(
        down =  feedBack.down,
        tempo = feedBack.tempo,
        back = feedBack.back,
        feedBackProblem =  feedBack.feedBackProblem,
        feedBackSolution =  feedBack.feedBackSolution,
        feedBackCheckListTitle = feedBack.feedBackCheckListTitle,
        feedBackCheckList =  feedBack.feedBackCheckList
    )
}
