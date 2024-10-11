package com.ijonsabae.data.usecase.login

import com.ijonsabae.data.repository.UserRepository
import com.ijonsabae.domain.usecase.login.SetAutoLoginStatusUseCase
import javax.inject.Inject

class SetAutoLoginStatusUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : SetAutoLoginStatusUseCase {
    override suspend operator fun invoke(autoLogin: Boolean){
        return userRepository.setAutoLoginStatus(autoLogin)
    }
}