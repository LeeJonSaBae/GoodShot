package com.ijonsabae.data.usecase.login

import com.ijonsabae.data.repository.TokenRepository
import com.ijonsabae.domain.usecase.login.GetLocalAccessTokenCreatedTimeUseCase
import javax.inject.Inject

class GetLocalAccessTokenCreatedTimeUseCaseImpl @Inject constructor(private val tokenRepository: TokenRepository): GetLocalAccessTokenCreatedTimeUseCase{
    override suspend operator fun invoke() : Long?{
        return tokenRepository.getLocalTokenCreatedTime()
    }
}