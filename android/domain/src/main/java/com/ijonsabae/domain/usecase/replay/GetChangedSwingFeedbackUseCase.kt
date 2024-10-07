package com.ijonsabae.domain.usecase.replay

import com.ijonsabae.domain.model.SwingFeedbackSyncRoomData

interface GetChangedSwingFeedbackUseCase {
    operator fun invoke(userID: Long): List<SwingFeedbackSyncRoomData>
}