package com.ijonsabae.data.usecase.login

import com.ijonsabae.data.retrofit.UserService
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.LoginParam
import com.ijonsabae.domain.model.Token
import com.ijonsabae.domain.usecase.login.LoginUseCase
import javax.inject.Inject

class LoginUseCaseImpl @Inject constructor(private val userService: UserService): LoginUseCase {
    override suspend operator fun invoke(loginParam: LoginParam): CommonResponse<Token> {
        val result = userService.login(loginParam)
        return userService.login(loginParam)
    }
}