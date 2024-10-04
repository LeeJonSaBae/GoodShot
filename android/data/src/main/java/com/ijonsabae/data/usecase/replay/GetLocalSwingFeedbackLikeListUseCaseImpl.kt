package com.ijonsabae.data.usecase.replay

import androidx.paging.PagingData
import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackLikeListUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocalSwingFeedbackLikeListUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
): GetLocalSwingFeedbackLikeListUseCase{
    override operator fun invoke(userID:Long): Flow<PagingData<SwingFeedback>> {
        return swingFeedbackRepository.getLikeSwingFeedbackList(userID = userID)
    }

}