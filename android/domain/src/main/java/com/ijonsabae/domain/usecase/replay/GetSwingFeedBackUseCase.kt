package com.ijonsabae.domain.usecase.replay

import com.ijonsabae.domain.model.FeedBack

interface GetSwingFeedBackUseCase {
    operator fun invoke() : Result<FeedBack>
}