package com.ijonsabae.data.usecase.login

import com.ijonsabae.data.repository.TokenRepository
import com.ijonsabae.domain.usecase.login.ClearLocalTokenUseCase
import javax.inject.Inject

class ClearLocalTokenUseCaseImpl @Inject constructor(private val tokenRepository: TokenRepository): ClearLocalTokenUseCase{
    override suspend operator fun invoke(){
        tokenRepository.clearToken()
    }
}