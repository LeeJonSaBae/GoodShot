package com.ijonsabae.presentation.mapper

import com.ijonsabae.domain.model.SwingFeedbackComment
import com.ijonsabae.presentation.model.SwingFeedbackCommentParcelable

object SwingFeedbackCommentMapper {
    private fun mapperToSwingFeedbackParcelable(swingFeedbackComment: SwingFeedbackComment): SwingFeedbackCommentParcelable{
        return SwingFeedbackCommentParcelable(
            userID = swingFeedbackComment.userID,
            swingCode = swingFeedbackComment.swingCode,
            commentType = swingFeedbackComment.commentType,
            poseType = swingFeedbackComment.poseType,
            content = swingFeedbackComment.content
        )
    }

    fun mapperToSwingFeedbackParcelableList(swingFeedbackCommentList: List<SwingFeedbackComment>): List<SwingFeedbackCommentParcelable>{
        return swingFeedbackCommentList.map {
            mapperToSwingFeedbackParcelable(it)
        }
    }
}