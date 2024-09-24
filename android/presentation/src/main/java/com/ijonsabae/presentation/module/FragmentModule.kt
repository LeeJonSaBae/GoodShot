package com.ijonsabae.presentation.module

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.window.layout.WindowInfoTracker
import com.ijonsabae.presentation.config.ToastHelper
import com.ijonsabae.presentation.shot.flex.FoldingStateActor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Named

@Module
@InstallIn(FragmentComponent::class)
class FragmentModule {
    @Named("fragment")
    @Provides
    fun provideFragmentToastHelper(fragment: Fragment): ToastHelper {
        return ToastHelper(fragment.requireContext())
    }
    @Provides
    fun provideFoldingStateActor(@ActivityContext context: Context):FoldingStateActor{
        return FoldingStateActor(WindowInfoTracker.getOrCreate(context))
    }
}