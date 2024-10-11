package com.ijonsabae.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.ijonsabae.domain.usecase.app.GetLocalAccessTokenFlowUseCase
import com.ijonsabae.domain.usecase.login.ClearLocalTokenUseCase
import com.ijonsabae.domain.usecase.login.GetAutoLoginStatusUseCase
import com.ijonsabae.domain.usecase.login.GetUserIdUseCase
import com.ijonsabae.domain.usecase.replay.UpdateUserIdUseCase
import com.ijonsabae.presentation.shot.SwingLocalDataProcessor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "DetectAccessTokenServic 싸피"
@AndroidEntryPoint
class DetectAccessTokenService : Service() {
    @Inject
    lateinit var getLocalAccessTokenFlowUseCase: GetLocalAccessTokenFlowUseCase
    @Inject
    lateinit var updateUserIdUseCase: UpdateUserIdUseCase
    @Inject
    lateinit var getUserIdUseCase: GetUserIdUseCase

    private lateinit var accessLoginTokenFlow: StateFlow<String?>

    private val binder = LocalBinder()

    // Binder 클래스, 클라이언트가 이 객체를 통해 서비스에 액세스할 수 있습니다.
    inner class LocalBinder : Binder() {
        fun getService(): DetectAccessTokenService = this@DetectAccessTokenService
    }

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch{
            coroutineScope {
                accessLoginTokenFlow = getLocalAccessTokenFlowUseCase()
            }
            initFlow()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private fun initFlow(){
        CoroutineScope(Dispatchers.IO).launch{
            accessLoginTokenFlow.collect{
                if(it != null){
                    val id = getUserIdUseCase()
                    updateUserIdUseCase(oldUserId = -1, newUserId = id)
                    SwingLocalDataProcessor.guestDataTransfer(baseContext, id)
                }
            }
        }
    }

}