package com.ijonsabae.domain.usecase.replay

import com.ijonsabae.domain.model.SwingFeedbackSyncRoomData

interface GetLocalChangedSwingFeedbackListUseCase {
    operator fun invoke(userID: Long): List<SwingFeedbackSyncRoomData>
}