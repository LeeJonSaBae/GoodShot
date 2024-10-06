package com.ijonsabae.data.usecase.replay

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.domain.usecase.replay.UpdateUserIdUseCase
import javax.inject.Inject

class UpdateUserIdUseCaseImpl @Inject constructor(
    private val feedbackRepository: SwingFeedbackRepository
): UpdateUserIdUseCase {
    override fun invoke(oldUserId: Long, newUserId: Long) : Int {
        return feedbackRepository.updateUserId(oldUserId, newUserId)
    }
}