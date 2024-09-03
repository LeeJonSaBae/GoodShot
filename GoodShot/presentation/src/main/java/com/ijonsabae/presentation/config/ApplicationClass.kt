package com.ijonsabae.presentation.config

import android.app.Application
import android.util.Log
import com.google.gson.Gson
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Inject

private const val TAG = "ApplicationClass_싸피"
@HiltAndroidApp
class ApplicationClass: Application() {
    companion object {
        //const val SERVER_URL = BuildConfig.SERVER_IP
    }
}