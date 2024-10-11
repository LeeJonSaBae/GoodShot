package com.ijonsabae.domain.usecase.login

import com.ijonsabae.domain.model.CommonResponse

interface SetLocalUserNameUseCase {
    suspend operator fun invoke(name: String)
}