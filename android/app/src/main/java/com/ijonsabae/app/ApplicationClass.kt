package com.ijonsabae.app

import android.app.Application
import android.content.Intent
import androidx.lifecycle.LifecycleObserver
import com.ijonsabae.service.DetectDestroyService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ApplicationClass: Application(), LifecycleObserver {
    companion object {
        //const val SERVER_URL = BuildConfig.SERVER_IP
    }

    override fun onCreate() {
        super.onCreate()
        startService(Intent(this, DetectDestroyService::class.java))

    }
}