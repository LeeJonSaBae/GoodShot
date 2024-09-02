package com.ijonsabae.goodshot.config

import android.app.Application
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "ApplicationClass_싸피"
@HiltAndroidApp
class ApplicationClass: Application() {
    companion object {
        //const val SERVER_URL = BuildConfig.SERVER_IP
        lateinit var retrofit: Retrofit
    }

    private lateinit var okHttpClient: OkHttpClient
    private lateinit var gson: Gson

    @Inject
    fun setOkHttpClient(okHttpClient: OkHttpClient){
        this.okHttpClient = okHttpClient
    }

    @Inject
    fun setGson(gson: Gson){
        this.gson = gson
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ${gson}")
        // 앱이 처음 생성되는 순간, retrofit 인스턴스를 생성
//        retrofit = Retrofit.Builder()
//            .baseUrl(SERVER_URL)
//            .addConverterFactory(ScalarsConverterFactory.create())
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .client(okHttpClient)
//            .build()


    }
}