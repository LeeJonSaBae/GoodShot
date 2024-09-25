package com.ijonsabae.data.di

import com.ijonsabae.data.usecase.shot.GetSwingFeedBackUseCaseImpl
import com.ijonsabae.domain.usecase.shot.GetSwingFeedBackUseCase
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