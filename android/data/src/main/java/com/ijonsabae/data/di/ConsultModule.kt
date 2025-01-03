package com.ijonsabae.data.di

import com.ijonsabae.data.repository.ConsultRepository
import com.ijonsabae.data.repository.ConsultRepositoryImpl
import com.ijonsabae.data.usecase.consult.GetConsultantInfoUseCaseImpl
import com.ijonsabae.data.usecase.consult.GetConsultantListUseCaseImpl
import com.ijonsabae.domain.usecase.consult.GetConsultantInfoUseCase
import com.ijonsabae.domain.usecase.consult.GetConsultantListUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ConsultModule {
    @Binds
    abstract fun bindGetConsultantListUseCase(uc: GetConsultantListUseCaseImpl): GetConsultantListUseCase

    @Binds
    abstract fun bindGetConsultantInfoUseCase(uc: GetConsultantInfoUseCaseImpl): GetConsultantInfoUseCase

    @Binds
    abstract fun bindGetConsultRepository(rp: ConsultRepositoryImpl): ConsultRepository
}