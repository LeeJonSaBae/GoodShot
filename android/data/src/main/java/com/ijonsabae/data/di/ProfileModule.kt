package com.ijonsabae.data.di

import com.ijonsabae.data.usecase.profile.ChangePasswordUseCaseImpl
import com.ijonsabae.data.usecase.profile.GetProfileImgUseCaseImpl
import com.ijonsabae.data.usecase.profile.GetProfileInfoUseCaseImpl
import com.ijonsabae.data.usecase.profile.GetTotalReportUseCaseImpl
import com.ijonsabae.data.usecase.profile.LogoutUseCaseImpl
import com.ijonsabae.data.usecase.profile.ResignUseCaseImpl
import com.ijonsabae.data.usecase.profile.UpdateProfileUseCaseImpl
import com.ijonsabae.data.usecase.profile.UploadPresignedDataUseCaseImpl
import com.ijonsabae.domain.usecase.profile.ChangePasswordUseCase
import com.ijonsabae.domain.usecase.profile.GetPresignedURLUseCase
import com.ijonsabae.domain.usecase.profile.GetProfileInfoUseCase
import com.ijonsabae.domain.usecase.profile.GetTotalReportUseCase
import com.ijonsabae.domain.usecase.profile.LogoutUseCase
import com.ijonsabae.domain.usecase.profile.ResignUseCase
import com.ijonsabae.domain.usecase.profile.UpdateProfileUseCase
import com.ijonsabae.domain.usecase.profile.UploadPresignedDataUseCase
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
    abstract fun binduploadProfileImageUseCase(uc: UploadPresignedDataUseCaseImpl): UploadPresignedDataUseCase

    @Binds
    abstract fun bindGetProfileInfoUseCase(uc: GetProfileInfoUseCaseImpl): GetProfileInfoUseCase

    @Binds
    abstract fun bindLogoutUseCase(uc: LogoutUseCaseImpl): LogoutUseCase

    @Binds
    abstract fun bindUpdateProfileUseCase(uc: UpdateProfileUseCaseImpl): UpdateProfileUseCase

    @Binds
    abstract fun bindChangePasswordUseCase(uc: ChangePasswordUseCaseImpl): ChangePasswordUseCase

    @Binds
    abstract fun bindResignUseCase(uc: ResignUseCaseImpl): ResignUseCase

    @Binds
    abstract fun bindGetTotalReportUseCase(uc: GetTotalReportUseCaseImpl): GetTotalReportUseCase
}