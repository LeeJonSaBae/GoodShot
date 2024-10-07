package com.ijonsabae.app

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleObserver
import com.ijonsabae.service.DetectAccessTokenService
import com.ijonsabae.service.DetectDestroyService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ApplicationClass: Application(), LifecycleObserver {
    companion object {
        //const val SERVER_URL = BuildConfig.SERVER_IP
    }
    private var service: DetectAccessTokenService? = null
    private var isBound = false
    // ServiceConnection 인터페이스 구현
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // 서비스에 연결되었을 때
            val binder = service as DetectAccessTokenService.LocalBinder
            this@ApplicationClass.service = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // 서비스가 비정상적으로 연결 해제되었을 때
            isBound = false
            service = null
        }
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        startService(Intent(this, DetectDestroyService::class.java))
        bindToService()
    }

    override fun onTerminate() {
        super.onTerminate()
        unbindService(serviceConnection)
    }

    private fun bindToService() {
        val intent = Intent(this, DetectAccessTokenService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
}