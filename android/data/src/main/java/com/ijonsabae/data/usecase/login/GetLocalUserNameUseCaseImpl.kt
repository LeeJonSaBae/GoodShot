package com.ijonsabae.data.usecase.login

import com.ijonsabae.data.repository.UserRepository
import com.ijonsabae.domain.usecase.login.GetLocalUserNameUseCase
import javax.inject.Inject

class GetLocalUserNameUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): GetLocalUserNameUseCase{
    override suspend fun invoke(): String? {
        return userRepository.getLocalUsername()
    }

}