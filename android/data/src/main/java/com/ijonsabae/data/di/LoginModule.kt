package com.ijonsabae.data.di

import com.ijonsabae.data.usecase.login.LoginUseCaseImpl
import com.ijonsabae.domain.usecase.login.LoginUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LoginModule {
    @Binds
    abstract fun bindLoginUseCase(uc: LoginUseCaseImpl):LoginUseCase
}