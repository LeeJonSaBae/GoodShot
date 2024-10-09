package com.ijonsabae.data.usecase.login

import com.ijonsabae.domain.model.Token
import com.ijonsabae.data.repository.TokenRepository
import com.ijonsabae.data.repository.UserRepository
import com.ijonsabae.domain.usecase.login.SetLocalTokenUseCase
import javax.inject.Inject

class SetLocalTokenUseCaseImpl @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository
) : SetLocalTokenUseCase {
    override suspend operator fun invoke(token: Token){
        userRepository.getLocalUsername()
        userRepository.getRemoteUsername().getOrNull()
        tokenRepository.setLocalToken(token)
    }
}