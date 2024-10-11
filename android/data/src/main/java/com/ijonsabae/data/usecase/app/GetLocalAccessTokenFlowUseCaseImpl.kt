package com.ijonsabae.data.usecase.app

import com.ijonsabae.data.repository.TokenRepository
import com.ijonsabae.domain.usecase.app.GetLocalAccessTokenFlowUseCase
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetLocalAccessTokenFlowUseCaseImpl @Inject constructor(
    private val tokenRepository: TokenRepository
): GetLocalAccessTokenFlowUseCase {
    override suspend operator fun invoke(): StateFlow<String?>{
        return tokenRepository.getLocalAccessTokenFlow()
    }
}