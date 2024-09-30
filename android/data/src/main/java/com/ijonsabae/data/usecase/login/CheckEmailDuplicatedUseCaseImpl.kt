package com.ijonsabae.data.usecase.login

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.repository.UserRepository
import com.ijonsabae.domain.usecase.login.CheckEmailDuplicatedUseCase
import javax.inject.Inject

class CheckEmailDuplicatedUseCaseImpl @Inject constructor(private val userRepository: UserRepository): CheckEmailDuplicatedUseCase {
    override suspend operator fun invoke(email: String): Result<CommonResponse<Boolean>> {
        return userRepository.checkEmailDuplicated(email)
    }
}