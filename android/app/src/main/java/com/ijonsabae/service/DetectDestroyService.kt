package com.ijonsabae.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.ijonsabae.domain.usecase.login.ClearLocalTokenUseCase
import com.ijonsabae.domain.usecase.login.GetAutoLoginStatusUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val TAG = "DetectDestroyService_싸피"
@AndroidEntryPoint
class DetectDestroyService : Service() {
    @Inject
    lateinit var getAutoLoginStatusUseCase: GetAutoLoginStatusUseCase
    @Inject
    lateinit var clearLocalTokenUseCase: ClearLocalTokenUseCase

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        runBlocking {
            if(!getAutoLoginStatusUseCase()){
                clearLocalTokenUseCase()
            }
        }
    }

}