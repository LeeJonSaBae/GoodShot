package com.ijonsabae.domain.model

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class SwingFeedbackSyncRoomData(
    @ColumnInfo("likeStatus")val likeStatus: Boolean,
    @ColumnInfo("title")val title: String,
    @ColumnInfo("swingCode")val code: String,
    @ColumnInfo("date")val time: Long,
    @ColumnInfo("isUpdated")val update: Int
)
