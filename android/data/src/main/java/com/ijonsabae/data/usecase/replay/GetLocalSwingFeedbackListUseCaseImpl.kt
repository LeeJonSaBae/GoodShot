package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackListUseCase
import javax.inject.Inject

class GetLocalSwingFeedbackListUseCaseImpl@Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
): GetLocalSwingFeedbackListUseCase{
    override operator fun invoke(userID: Long): List<SwingFeedback>{
        return swingFeedbackRepository.getAllSwingFeedbackList(userID)
    }
}