package com.ijonsabae.data.usecase.login

import com.ijonsabae.domain.repository.UserRepository
import com.ijonsabae.domain.usecase.login.GetAutoLoginStatusUseCase
import javax.inject.Inject

class GetAutoLoginStatusUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : GetAutoLoginStatusUseCase{
    override suspend operator fun invoke(): Boolean{
        return userRepository.getAutoLoginStatus()
    }
}