package com.ijonsabae.domain.usecase.replay

import com.ijonsabae.domain.model.Replay

interface GetReplayUseCase {
    operator fun invoke(): Result<List<Replay>>

}