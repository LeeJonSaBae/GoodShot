package com.ijonsabae.data.usecase.login

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.RegisterParam
import com.ijonsabae.data.repository.UserRepository
import com.ijonsabae.domain.usecase.login.RegisterUseCase
import javax.inject.Inject

class RegisterUseCaseImpl @Inject constructor(private val userRepository: UserRepository) : RegisterUseCase {
    override suspend fun invoke(registerParam: RegisterParam): Result<CommonResponse<Unit>> {
        return userRepository.join(registerParam)
    }
}