package com.ijonsabae.data.usecase.profile

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.repository.UserRepository
import com.ijonsabae.domain.usecase.profile.LogoutUseCase
import javax.inject.Inject

class LogoutUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenRepository: UserRepository
) : LogoutUseCase {

    override suspend fun invoke(): Result<CommonResponse<Unit>> {
        tokenRepository.logout()
        return userRepository.logout()
    }
}