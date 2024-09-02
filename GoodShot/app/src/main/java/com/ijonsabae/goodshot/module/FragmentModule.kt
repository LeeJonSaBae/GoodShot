package com.ijonsabae.goodshot.module

import androidx.fragment.app.Fragment
import com.ijonsabae.goodshot.config.ToastHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import javax.inject.Named

@Module
@InstallIn(FragmentComponent::class)
class FragmentModule {
    @Named("fragment")
    @Provides
    fun provideFragmentToastHelper(fragment: Fragment): ToastHelper {
        return ToastHelper(fragment.requireContext())
    }
}