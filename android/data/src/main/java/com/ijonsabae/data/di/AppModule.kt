package com.ijonsabae.data.di

import com.ijonsabae.data.usecase.app.GetLocalAccessTokenFlowUseCaseImpl
import com.ijonsabae.domain.usecase.app.GetLocalAccessTokenFlowUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    abstract fun bindGetLocalAccessTokenFlowUseCase(uc: GetLocalAccessTokenFlowUseCaseImpl): GetLocalAccessTokenFlowUseCase
}