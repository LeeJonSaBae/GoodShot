package com.ijonsabae.data.di

import com.ijonsabae.data.repository.TokenRepositoryImpl
import com.ijonsabae.data.repository.UserRepositoryImpl
import com.ijonsabae.data.usecase.login.CheckEmailDuplicatedUseCaseImpl
import com.ijonsabae.data.usecase.login.ClearLocalTokenUseCaseImpl
import com.ijonsabae.data.usecase.login.GenerateTemporaryPassWordUseCaseImpl
import com.ijonsabae.data.usecase.login.GetAutoLoginStatusUseCaseImpl
import com.ijonsabae.data.usecase.login.GetLocalAccessTokenCreatedTimeUseCaseImpl
import com.ijonsabae.data.usecase.login.GetLocalAccessTokenUseCaseImpl
import com.ijonsabae.data.usecase.login.GetLocalRefreshTokenUseCaseImpl
import com.ijonsabae.data.usecase.login.GetUserIdUseCaseImpl
import com.ijonsabae.data.usecase.login.LoginUseCaseImpl
import com.ijonsabae.data.usecase.login.RegisterUseCaseImpl
import com.ijonsabae.data.usecase.login.ReissueTokenUseCaseImpl
import com.ijonsabae.data.usecase.login.RequestEmailAuthCodeUseCaseImpl
import com.ijonsabae.data.usecase.login.SetAutoLoginStatusUseCaseImpl
import com.ijonsabae.data.usecase.login.SetLocalTokenUseCaseImpl
import com.ijonsabae.data.usecase.login.VerifyEmailAuthCodeUseCaseImpl
import com.ijonsabae.domain.repository.TokenRepository
import com.ijonsabae.domain.repository.UserRepository
import com.ijonsabae.domain.usecase.login.CheckEmailDuplicatedUseCase
import com.ijonsabae.domain.usecase.login.ClearLocalTokenUseCase
import com.ijonsabae.domain.usecase.login.GenerateTemporaryPassWordUseCase
import com.ijonsabae.domain.usecase.login.GetAutoLoginStatusUseCase
import com.ijonsabae.domain.usecase.login.GetLocalAccessTokenCreatedTimeUseCase
import com.ijonsabae.domain.usecase.login.GetLocalAccessTokenUseCase
import com.ijonsabae.domain.usecase.login.GetLocalRefreshTokenUseCase
import com.ijonsabae.domain.usecase.login.GetUserIdUseCase
import com.ijonsabae.domain.usecase.login.LoginUseCase
import com.ijonsabae.domain.usecase.login.RegisterUseCase
import com.ijonsabae.domain.usecase.login.ReissueTokenUseCase
import com.ijonsabae.domain.usecase.login.RequestEmailAuthCodeUseCase
import com.ijonsabae.domain.usecase.login.SetAutoLoginStatusUseCase
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
    abstract fun bindGetLocalUserIdUseCase(uc: GetUserIdUseCaseImpl): GetUserIdUseCase

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
    abstract fun bindCheckEmailDuplicatedUseCase(uc: CheckEmailDuplicatedUseCaseImpl): CheckEmailDuplicatedUseCase

    @Binds
    abstract fun bindGenerateTemporaryPassWordUseCase(uc: GenerateTemporaryPassWordUseCaseImpl): GenerateTemporaryPassWordUseCase

    @Binds
    abstract fun bindGetAutoLoginStatusUseCase(uc: GetAutoLoginStatusUseCaseImpl): GetAutoLoginStatusUseCase

    @Binds
    abstract fun bindSetAutoLoginStatusUseCase(uc: SetAutoLoginStatusUseCaseImpl): SetAutoLoginStatusUseCase

    @Binds
    abstract fun bindUserRepository(uc: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindTokenRepository(uc: TokenRepositoryImpl): TokenRepository
}