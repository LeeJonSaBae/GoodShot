package com.ijonsabae.data.usecase.login

import com.ijonsabae.domain.model.Token
import com.ijonsabae.domain.repository.TokenRepository
import com.ijonsabae.domain.usecase.login.SetLocalTokenUseCase
import javax.inject.Inject

class SetLocalTokenUseCaseImpl @Inject constructor(private val tokenRepository: TokenRepository) : SetLocalTokenUseCase {
    override suspend operator fun invoke(token: Token){
        tokenRepository.setLocalToken(token)
    }
}