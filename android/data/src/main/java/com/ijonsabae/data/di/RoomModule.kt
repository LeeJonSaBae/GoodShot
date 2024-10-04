package com.ijonsabae.data.di

import android.content.Context
import com.ijonsabae.data.dao.SwingFeedbackCommentDao
import com.ijonsabae.data.dao.SwingFeedbackDao
import com.ijonsabae.data.database.SwingFeedbackDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {
    @Singleton
    @Provides
    fun provideSwingFeedbackDatabase(
        @ApplicationContext context: Context,
    ): SwingFeedbackDatabase =
        SwingFeedbackDatabase.getInstance(context)

    @Singleton
    @Provides
    fun provideSwingFeedbackDao(swingFeedbackDatabase: SwingFeedbackDatabase): SwingFeedbackDao
        = swingFeedbackDatabase.swingFeedbackDao()

    @Singleton
    @Provides
    fun provideSwingFeedbackCommentDao(swingFeedbackDatabase: SwingFeedbackDatabase): SwingFeedbackCommentDao
            = swingFeedbackDatabase.swingFeedbackCommentDao()

}