package com.ijonsabae.data.di

import com.ijonsabae.data.usecase.profile.GetProfileImgUseCaseImpl
import com.ijonsabae.data.usecase.profile.UploadProfileImageUseCaseImpl
import com.ijonsabae.domain.usecase.profile.GetPresignedURLUseCase
import com.ijonsabae.domain.usecase.profile.UploadProfileImageUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {
    @Binds
    abstract fun bindGetPresignedURLUseCase(uc: GetProfileImgUseCaseImpl): GetPresignedURLUseCase

    @Binds
    abstract fun binduploadProfileImageUseCase(uc: UploadProfileImageUseCaseImpl): UploadProfileImageUseCase
}