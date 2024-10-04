package com.ijonsabae.data.di

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.data.repository.SwingFeedbackRepositoryImpl
import com.ijonsabae.data.usecase.replay.DeleteLocalSwingFeedbackCommentUseCaseImpl
import com.ijonsabae.data.usecase.replay.DeleteLocalSwingFeedbackUseCaseImpl
import com.ijonsabae.data.usecase.replay.GetLocalSwingFeedbackCommentUseCaseImpl
import com.ijonsabae.data.usecase.replay.GetLocalSwingFeedbackLikeListUseCaseImpl
import com.ijonsabae.data.usecase.replay.GetLocalSwingFeedbackListUseCaseImpl
import com.ijonsabae.data.usecase.replay.GetLocalSwingFeedbackUseCaseImpl
import com.ijonsabae.data.usecase.replay.GetReplayUseCaseImpl
import com.ijonsabae.data.usecase.replay.InsertLocalSwingFeedbackCommentUseCaseImpl
import com.ijonsabae.data.usecase.replay.InsertLocalSwingFeedbackUseCaseImpl
import com.ijonsabae.data.usecase.replay.UpdateUserIdUseCaseImpl
import com.ijonsabae.domain.usecase.replay.DeleteLocalSwingFeedbackCommentUseCase
import com.ijonsabae.domain.usecase.replay.DeleteLocalSwingFeedbackUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackCommentUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackLikeListUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackListUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackUseCase
import com.ijonsabae.domain.usecase.replay.GetReplayUseCase
import com.ijonsabae.domain.usecase.replay.InsertLocalSwingFeedbackCommentUseCase
import com.ijonsabae.domain.usecase.replay.InsertLocalSwingFeedbackUseCase
import com.ijonsabae.domain.usecase.replay.UpdateUserIdUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ReplayModule {
    @Binds
    abstract fun bindGetReplayUseCase(uc: GetReplayUseCaseImpl): GetReplayUseCase

    @Binds
    abstract fun bindGetLocalSwingFeedbackListUseCase(uc: GetLocalSwingFeedbackListUseCaseImpl): GetLocalSwingFeedbackListUseCase

    @Binds
    abstract fun bindGetLocalSwingFeedbackUseCase(uc: GetLocalSwingFeedbackUseCaseImpl): GetLocalSwingFeedbackUseCase

    @Binds
    abstract fun bindGetLocalSwingFeedbackLikeListUseCase(uc: GetLocalSwingFeedbackLikeListUseCaseImpl): GetLocalSwingFeedbackLikeListUseCase

    @Binds
    abstract fun bindGetLocalSwingFeedbackCommentUseCase(uc: GetLocalSwingFeedbackCommentUseCaseImpl): GetLocalSwingFeedbackCommentUseCase

    @Binds
    abstract fun bindDeleteLocalSwingFeedbackUseCase(uc: DeleteLocalSwingFeedbackUseCaseImpl): DeleteLocalSwingFeedbackUseCase

    @Binds
    abstract fun bindDeleteLocalSwingFeedbackCommentUseCase(uc: DeleteLocalSwingFeedbackCommentUseCaseImpl): DeleteLocalSwingFeedbackCommentUseCase

    @Binds
    abstract fun bindInsertLocalSwingFeedbackUseCase(uc: InsertLocalSwingFeedbackUseCaseImpl): InsertLocalSwingFeedbackUseCase

    @Binds
    abstract fun bindInsertLocalSwingFeedbackCommentUseCase(uc: InsertLocalSwingFeedbackCommentUseCaseImpl): InsertLocalSwingFeedbackCommentUseCase

    @Binds
    abstract fun bindUpdateUserIdUseCaseUseCase(uc: UpdateUserIdUseCaseImpl): UpdateUserIdUseCase

    @Binds
    abstract fun bindSwingFeedbackRepository(rp: SwingFeedbackRepositoryImpl): SwingFeedbackRepository
}
