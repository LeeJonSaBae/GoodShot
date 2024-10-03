package com.ijonsabae.data.di

import com.ijonsabae.data.repository.YoutubeRepository
import com.ijonsabae.data.repository.YoutubeRepositoryImpl
import com.ijonsabae.data.usecase.home.GetSearchVideosUseCaseImpl
import com.ijonsabae.domain.usecase.home.GetSearchVideosUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {
    @Binds
    abstract fun bindGetSearchVideosUseCase(uc: GetSearchVideosUseCaseImpl): GetSearchVideosUseCase

    @Binds
    abstract fun bindYoutubeRepository(rp: YoutubeRepositoryImpl): YoutubeRepository
}