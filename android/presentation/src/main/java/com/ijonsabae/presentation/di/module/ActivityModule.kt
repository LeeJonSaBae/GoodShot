package com.ijonsabae.presentation.di.module

import android.app.Activity
import com.ijonsabae.presentation.config.ToastHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Named

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {
    @Named("activity")
    @Provides
    fun provideActivityToastHelper(activity: Activity): ToastHelper {
        return ToastHelper(activity)
    }
}