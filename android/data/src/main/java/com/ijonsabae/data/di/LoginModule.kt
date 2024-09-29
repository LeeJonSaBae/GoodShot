package com.ijonsabae.data.di

import com.ijonsabae.data.repository.TokenRepositoryImpl
import com.ijonsabae.data.repository.UserRepositoryImpl
import com.ijonsabae.data.usecase.login.ClearLocalTokenUseCaseImpl
import com.ijonsabae.data.usecase.login.GetLocalAccessTokenCreatedTimeUseCaseImpl
import com.ijonsabae.data.usecase.login.GetLocalAccessTokenUseCaseImpl
import com.ijonsabae.data.usecase.login.GetLocalRefreshTokenUseCaseImpl
import com.ijonsabae.data.usecase.login.LoginUseCaseImpl
import com.ijonsabae.data.usecase.login.RegisterUseCaseImpl
import com.ijonsabae.data.usecase.login.ReissueTokenUseCaseImpl
import com.ijonsabae.data.usecase.login.RequestEmailAuthCodeUseCaseImpl
import com.ijonsabae.data.usecase.login.SetLocalTokenUseCaseImpl
import com.ijonsabae.data.usecase.login.VerifyEmailAuthCodeUseCaseImpl
import com.ijonsabae.domain.repository.TokenRepository
import com.ijonsabae.domain.repository.UserRepository
import com.ijonsabae.domain.usecase.login.ClearLocalTokenUseCase
import com.ijonsabae.domain.usecase.login.GetLocalAccessTokenCreatedTimeUseCase
import com.ijonsabae.domain.usecase.login.GetLocalAccessTokenUseCase
import com.ijonsabae.domain.usecase.login.GetLocalRefreshTokenUseCase
import com.ijonsabae.domain.usecase.login.LoginUseCase
import com.ijonsabae.domain.usecase.login.RegisterUseCase
import com.ijonsabae.domain.usecase.login.ReissueTokenUseCase
import com.ijonsabae.domain.usecase.login.RequestEmailAuthCodeUseCase
import com.ijonsabae.domain.usecase.login.SetLocalTokenUseCase
import com.ijonsabae.domain.usecase.login.VerifyEmailAuthCodeUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LoginModule {
    @Binds
    abstract fun bindLoginUseCase(uc: LoginUseCaseImpl):LoginUseCase

    @Binds
    abstract fun bindGetLocalAccessTokenUseCase(uc: GetLocalAccessTokenUseCaseImpl): GetLocalAccessTokenUseCase

    @Binds
    abstract fun bindGetLocalRefreshTokenUseCase(uc: GetLocalRefreshTokenUseCaseImpl): GetLocalRefreshTokenUseCase

    @Binds
    abstract fun bindSetLocalAccessTokenUseCase(uc: SetLocalTokenUseCaseImpl): SetLocalTokenUseCase

    @Binds
    abstract fun bindGetLocalAccessTokenCreatedTimeUseCase(uc: GetLocalAccessTokenCreatedTimeUseCaseImpl): GetLocalAccessTokenCreatedTimeUseCase

    @Binds
    abstract fun bindReissueTokenUseCase(uc: ReissueTokenUseCaseImpl): ReissueTokenUseCase

    @Binds
    abstract fun bindClearLocalTokenUseCase(uc: ClearLocalTokenUseCaseImpl): ClearLocalTokenUseCase

    @Binds
    abstract fun bindRegisterUseCase(uc: RegisterUseCaseImpl): RegisterUseCase

    @Binds
    abstract fun bindRequestEmailAuthCodeUseCase(uc: RequestEmailAuthCodeUseCaseImpl): RequestEmailAuthCodeUseCase

    @Binds
    abstract fun bindVerifyEmailAuthCodeUseCase(uc: VerifyEmailAuthCodeUseCaseImpl): VerifyEmailAuthCodeUseCase

    @Binds
    abstract fun bindUserRepository(uc: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindTokenRepository(uc: TokenRepositoryImpl): TokenRepository
}