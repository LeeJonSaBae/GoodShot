package com.ijonsabae.data.usecase.login

import com.ijonsabae.data.repository.TokenRepository
import com.ijonsabae.domain.usecase.login.GetUserIdUseCase
import javax.inject.Inject

class GetUserIdUseCaseImpl @Inject constructor(
    private val tokenRepository: TokenRepository
): GetUserIdUseCase {
    override suspend operator fun invoke(): Long{
        return tokenRepository.getUserId()
    }
}