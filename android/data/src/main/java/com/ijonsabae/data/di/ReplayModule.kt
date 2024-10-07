package com.ijonsabae.data.di

import com.ijonsabae.data.repository.SwingFeedbackRepository
import com.ijonsabae.data.repository.SwingFeedbackRepositoryImpl
import com.ijonsabae.data.usecase.replay.DeleteLocalSwingFeedbackCommentUseCaseImpl
import com.ijonsabae.data.usecase.replay.DeleteLocalSwingFeedbackUseCaseImpl
import com.ijonsabae.data.usecase.replay.GetChangedSwingFeedbackUseCaseImpl
import com.ijonsabae.data.usecase.replay.GetLocalSwingFeedbackCommentUseCaseImpl
import com.ijonsabae.data.usecase.replay.GetLocalSwingFeedbackLikeListUseCaseImpl
import com.ijonsabae.data.usecase.replay.GetLocalSwingFeedbackListUseCaseImpl
import com.ijonsabae.data.usecase.replay.GetLocalSwingFeedbackUseCaseImpl
import com.ijonsabae.data.usecase.replay.GetReplayUseCaseImpl
import com.ijonsabae.data.usecase.replay.HideSwingFeedbackUseCaseImpl
import com.ijonsabae.data.usecase.replay.InsertLocalSwingFeedbackUseCaseImpl
import com.ijonsabae.data.usecase.replay.UpdateClampStatusUseCaseImpl
import com.ijonsabae.data.usecase.replay.UpdateLikeStatusUseCaseImpl
import com.ijonsabae.data.usecase.replay.UpdateTitleUseCaseImpl
import com.ijonsabae.data.usecase.replay.UpdateUserIdUseCaseImpl
import com.ijonsabae.data.usecase.shot.InsertLocalSwingFeedbackCommentUseCaseImpl
import com.ijonsabae.domain.usecase.replay.DeleteLocalSwingFeedbackCommentUseCase
import com.ijonsabae.domain.usecase.replay.DeleteLocalSwingFeedbackUseCase
import com.ijonsabae.domain.usecase.replay.GetChangedSwingFeedbackUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackCommentUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackLikeListUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackListUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackUseCase
import com.ijonsabae.domain.usecase.replay.GetReplayUseCase
import com.ijonsabae.domain.usecase.replay.HideSwingFeedbackUseCase
import com.ijonsabae.domain.usecase.replay.UpdateClampStatusUseCase
import com.ijonsabae.domain.usecase.replay.UpdateLikeStatusUseCase
import com.ijonsabae.domain.usecase.replay.UpdateTitleUseCase
import com.ijonsabae.domain.usecase.replay.UpdateUserIdUseCase
import com.ijonsabae.domain.usecase.shot.InsertLocalSwingFeedbackCommentUseCase
import com.ijonsabae.domain.usecase.shot.InsertLocalSwingFeedbackUseCase
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
    abstract fun bindUpdateUserIdUseCase(uc: UpdateUserIdUseCaseImpl): UpdateUserIdUseCase

    @Binds
    abstract fun bindUpdateLikeStatusUseCase(uc: UpdateLikeStatusUseCaseImpl): UpdateLikeStatusUseCase

    @Binds
    abstract fun bindUpdateClampStatusUseCase(uc: UpdateClampStatusUseCaseImpl): UpdateClampStatusUseCase

    @Binds
    abstract fun bindUpdateTitleUseCase(uc: UpdateTitleUseCaseImpl): UpdateTitleUseCase

    @Binds
    abstract fun bindGetChangedSwingFeedbackUseCase(uc: GetChangedSwingFeedbackUseCaseImpl): GetChangedSwingFeedbackUseCase

    @Binds
    abstract fun bindHideSwingFeedbackUseCase(uc: HideSwingFeedbackUseCaseImpl): HideSwingFeedbackUseCase

    @Binds
    abstract fun bindSwingFeedbackRepository(rp: SwingFeedbackRepositoryImpl): SwingFeedbackRepository
}
