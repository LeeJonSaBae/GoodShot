package com.ijonsabae.domain.usecase.replay

import com.ijonsabae.domain.model.SwingFeedbackSyncRoomData

interface GetLocalChangedSwingFeedbackListUseCase {
    suspend operator fun invoke(userID: Long): List<SwingFeedbackSyncRoomData>
}