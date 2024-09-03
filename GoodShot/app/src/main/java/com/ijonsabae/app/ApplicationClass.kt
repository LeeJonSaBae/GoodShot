package com.ijonsabae.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ApplicationClass: Application() {
    companion object {
        //const val SERVER_URL = BuildConfig.SERVER_IP
    }
}