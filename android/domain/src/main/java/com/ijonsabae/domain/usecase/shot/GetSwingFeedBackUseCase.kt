package com.ijonsabae.domain.usecase.shot

import com.ijonsabae.domain.model.FeedBack

interface GetSwingFeedBackUseCase {
    operator fun invoke() : Result<FeedBack>
}