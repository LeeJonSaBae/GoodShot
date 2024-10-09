package com.ijonsabae.data.usecase.login

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Token
import com.ijonsabae.data.repository.TokenRepository
import com.ijonsabae.domain.usecase.login.ReissueTokenUseCase
import javax.inject.Inject

class ReissueTokenUseCaseImpl @Inject constructor(private val tokenRepository: TokenRepository) : ReissueTokenUseCase {
    override suspend operator fun invoke() : Result<CommonResponse<Token>> {
        return tokenRepository.reissueRemoteToken()
    }
}