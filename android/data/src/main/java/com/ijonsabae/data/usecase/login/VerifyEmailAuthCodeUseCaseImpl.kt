package com.ijonsabae.data.usecase.login

import com.ijonsabae.domain.model.CheckCode
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.repository.UserRepository
import com.ijonsabae.domain.usecase.login.VerifyEmailAuthCodeUseCase
import javax.inject.Inject

class VerifyEmailAuthCodeUseCaseImpl @Inject constructor(private val userRepository: UserRepository): VerifyEmailAuthCodeUseCase {
    override suspend operator fun invoke(email: String, emailAuthCode: String): Result<CommonResponse<CheckCode>> {
        return userRepository.verifyEmailAuthCode(email, emailAuthCode)
    }
}