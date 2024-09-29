package com.ijonsabae.data.di

import com.ijonsabae.data.usecase.profile.GetProfileImgUseCaseImpl
import com.ijonsabae.domain.usecase.profile.GetProfileImgUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {
    @Binds
    abstract fun bindGetProfileImgUseCase(uc: GetProfileImgUseCaseImpl): GetProfileImgUseCase
}