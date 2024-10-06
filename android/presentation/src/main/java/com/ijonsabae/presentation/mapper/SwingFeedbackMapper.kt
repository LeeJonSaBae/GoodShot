package com.ijonsabae.presentation.mapper

import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.model.SwingFeedbackComment
import com.ijonsabae.presentation.model.SwingFeedbackCommentParcelable
import com.ijonsabae.presentation.model.SwingFeedbackSerializable

object SwingFeedbackMapper {
    fun mapperToSwingFeedbackSerializable(swingFeedback: SwingFeedback): SwingFeedbackSerializable{
        return SwingFeedbackSerializable(
            userID = swingFeedback.userID,
            swingCode = swingFeedback.swingCode,
            tempo = swingFeedback.tempo,
            date = swingFeedback.date,
            isClamped = swingFeedback.isClamped,
            likeStatus = swingFeedback.likeStatus,
            similarity = swingFeedback.similarity,
            score = swingFeedback.score,
            title = swingFeedback.title,
            solution = swingFeedback.solution
        )
    }
}