package com.ijonsabae.data.di

import com.ijonsabae.data.usecase.replay.GetSwingFeedBackUseCaseImpl
import com.ijonsabae.domain.usecase.replay.GetSwingFeedBackUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ShotModule {
    @Binds
    abstract fun  bindGetSwingFeedBackUseCase(uc: GetSwingFeedBackUseCaseImpl) : GetSwingFeedBackUseCase
}