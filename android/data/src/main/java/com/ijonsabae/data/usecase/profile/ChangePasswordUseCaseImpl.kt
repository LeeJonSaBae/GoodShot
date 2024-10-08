package com.ijonsabae.data.usecase.profile

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.repository.UserRepository
import com.ijonsabae.domain.usecase.profile.ChangePasswordUseCase
import javax.inject.Inject

class ChangePasswordUseCaseImpl @Inject constructor(private val userRepository: UserRepository) : ChangePasswordUseCase{
    override suspend operator fun invoke(oldPassword: String, newPassword: String) : Result<CommonResponse<Unit>>{
        return userRepository.changePassword(oldPassword, newPassword)
    }
}