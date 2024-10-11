package com.ijonsabae.data.usecase.login

import com.ijonsabae.data.repository.UserRepository
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.usecase.login.SetLocalUserNameUseCase
import javax.inject.Inject

class SetLocalUserNameUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): SetLocalUserNameUseCase{
    override suspend operator fun invoke(name: String){
        userRepository.setLocalUserName(name)
    }
}