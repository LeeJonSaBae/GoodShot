package com.ijonsabae.data.usecase.login

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.repository.UserRepository
import com.ijonsabae.domain.usecase.login.RequestEmailAuthCodeUseCase
import javax.inject.Inject

class RequestEmailAuthCodeUseCaseImpl @Inject constructor(private val userRepository: UserRepository) :
    RequestEmailAuthCodeUseCase {
    override suspend operator fun invoke(email: String): Result<CommonResponse<Unit>> {
        return userRepository.requestEmailAuthCode(email)
    }
}