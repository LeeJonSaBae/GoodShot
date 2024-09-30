package com.ijonsabae.data.usecase.login

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.LoginParam
import com.ijonsabae.domain.model.Token
import com.ijonsabae.domain.repository.UserRepository
import com.ijonsabae.domain.usecase.login.LoginUseCase
import javax.inject.Inject

class LoginUseCaseImpl @Inject constructor(private val userRepository: UserRepository): LoginUseCase {
    override suspend operator fun invoke(loginParam: LoginParam): Result<CommonResponse<Token>> {
        return userRepository.login(loginParam)
    }
}