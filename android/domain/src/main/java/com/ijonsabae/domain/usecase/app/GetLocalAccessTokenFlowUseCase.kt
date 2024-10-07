package com.ijonsabae.domain.usecase.app

import kotlinx.coroutines.flow.StateFlow

interface GetLocalAccessTokenFlowUseCase {
    suspend operator fun invoke(): StateFlow<String?>
}