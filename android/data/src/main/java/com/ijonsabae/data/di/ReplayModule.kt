package com.ijonsabae.data.di

import com.ijonsabae.data.usecase.replay.GetReplayUseCaseImpl
import com.ijonsabae.domain.usecase.replay.GetReplayUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ReplayModule {
    @Binds
    abstract fun bindGetReplayUseCase(uc: GetReplayUseCaseImpl): GetReplayUseCase
}