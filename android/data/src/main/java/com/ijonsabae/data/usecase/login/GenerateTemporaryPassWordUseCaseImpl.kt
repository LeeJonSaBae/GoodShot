package com.ijonsabae.data.usecase.login

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.repository.UserRepository
import com.ijonsabae.domain.usecase.login.GenerateTemporaryPassWordUseCase
import javax.inject.Inject

class GenerateTemporaryPassWordUseCaseImpl @Inject constructor(private val userRepository: UserRepository) : GenerateTemporaryPassWordUseCase{
    override suspend fun invoke(name: String, email: String): Result<CommonResponse<Unit>> {
        return userRepository.generateTemporaryPassWord(name, email)
    }
}