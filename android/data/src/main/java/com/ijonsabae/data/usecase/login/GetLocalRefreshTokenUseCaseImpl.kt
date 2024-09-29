package com.ijonsabae.data.usecase.login

import com.ijonsabae.domain.repository.TokenRepository
import com.ijonsabae.domain.usecase.login.GetLocalRefreshTokenUseCase
import javax.inject.Inject

class GetLocalRefreshTokenUseCaseImpl @Inject constructor(private val tokenRepository: TokenRepository) :
    GetLocalRefreshTokenUseCase {
    override suspend fun invoke(): String? {
        return tokenRepository.getLocalRefreshToken()
    }
}