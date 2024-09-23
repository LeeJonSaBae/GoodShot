package com.ijonsabae.domain.usecase.consult

import com.ijonsabae.domain.model.Consultant

interface GetConsultantListUseCase {
    operator fun invoke(): Result<List<Consultant>>
}