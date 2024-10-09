package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.usecase.replay.HideSwingFeedbackUseCase
import javax.inject.Inject

class HideSwingFeedbackUseCaseImpl @Inject constructor(
    private val repository: SwingFeedbackRepository
): HideSwingFeedbackUseCase{
    override suspend operator fun invoke(userID: Long, swingCode: String, currentTime: Long) : Int{
        return repository.hideSwingFeedback(userID, swingCode, currentTime)
    }
}