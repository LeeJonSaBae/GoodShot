package com.ijonsabae.domain.usecase.replay

import androidx.paging.PagingData
import com.ijonsabae.domain.model.SwingFeedback
import kotlinx.coroutines.flow.Flow

interface GetLocalSwingFeedbackListUseCase {
    operator fun invoke(userID: Long): Flow<PagingData<SwingFeedback>>
}