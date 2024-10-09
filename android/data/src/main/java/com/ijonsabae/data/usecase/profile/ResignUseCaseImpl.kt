package com.ijonsabae.data.usecase.profile

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.data.repository.UserRepository
import com.ijonsabae.domain.usecase.profile.ResignUseCase
import javax.inject.Inject

class ResignUseCaseImpl @Inject constructor(private val userRepository: UserRepository): ResignUseCase{
    override suspend operator fun invoke(): Result<CommonResponse<Unit>>{
        return userRepository.resign()
    }
}