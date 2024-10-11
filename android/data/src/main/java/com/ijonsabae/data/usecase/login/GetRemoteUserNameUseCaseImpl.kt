package com.ijonsabae.data.usecase.login

import com.ijonsabae.data.repository.UserRepository
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.usecase.login.GetRemoteUserNameUseCase
import javax.inject.Inject

class GetRemoteUserNameUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): GetRemoteUserNameUseCase{
    override suspend fun invoke(): Result<CommonResponse<String>> {
        return userRepository.getRemoteUsername()
    }

}