package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.usecase.replay.InsertLocalSwingFeedbackUseCase
import javax.inject.Inject

class InsertLocalSwingFeedbackUseCaseImpl @Inject constructor(
    private val swingFeedbackRepository: SwingFeedbackRepository
) : InsertLocalSwingFeedbackUseCase{
    override operator fun invoke(swingFeedbackEntity: SwingFeedback){
        return swingFeedbackRepository.insertSwingFeedback(swingFeedbackEntity)
    }
}