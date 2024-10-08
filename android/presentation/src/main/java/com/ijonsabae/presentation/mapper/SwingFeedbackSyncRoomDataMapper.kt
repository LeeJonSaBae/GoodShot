package com.ijonsabae.presentation.mapper

import com.ijonsabae.domain.model.SwingFeedbackSync
import com.ijonsabae.domain.model.SwingFeedbackSyncRoomData
import com.ijonsabae.presentation.util.formatDateFromLongKorea
import com.ijonsabae.presentation.util.formatTDateFromLongKorea

object SwingFeedbackSyncRoomDataMapper {
    private fun mapperToSwingFeedbackSync(swingFeedbackSyncRoomData: SwingFeedbackSyncRoomData): SwingFeedbackSync{
        return SwingFeedbackSync(
            title = swingFeedbackSyncRoomData.title,
            code = swingFeedbackSyncRoomData.code,
            time = formatTDateFromLongKorea(swingFeedbackSyncRoomData.time),
            likeStatus = swingFeedbackSyncRoomData.likeStatus,
            update = swingFeedbackSyncRoomData.update
        )
    }

    fun mapperToSwingFeedbackSyncList(swingFeedbackSyncRoomDataList: List<SwingFeedbackSyncRoomData>): List<SwingFeedbackSync> {
        return swingFeedbackSyncRoomDataList.map {
            mapperToSwingFeedbackSync(it)
        }
    }
}