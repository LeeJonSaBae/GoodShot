package com.ijonsabae.data.usecase.login

import com.ijonsabae.domain.repository.TokenRepository
import com.ijonsabae.domain.usecase.login.GetLocalAccessTokenUseCase
import javax.inject.Inject

class GetLocalAccessTokenUseCaseImpl @Inject constructor(private val tokenRepository: TokenRepository) : GetLocalAccessTokenUseCase {
    override suspend fun invoke(): String? {
        return tokenRepository.getLocalAccessToken()
    }
}