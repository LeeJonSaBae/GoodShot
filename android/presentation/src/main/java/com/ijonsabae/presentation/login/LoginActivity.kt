package com.ijonsabae.presentation.login

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ijonsabae.domain.model.Token
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseActivity
import com.ijonsabae.presentation.databinding.ActivityLoginBinding
import com.ijonsabae.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    private lateinit var splashScreen: SplashScreen
    private val loginViewModel: LoginViewModel by viewModels()
    private var waiting = true
    // 화면 전환 완료되면 false 값을 보내줄 것임,
    // 이 값을 waiting으로 설정해서 splash Screen의 대기 화면을 종료시킴
    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context, intent: Intent) {
            waiting = intent.getBooleanExtra("complete", true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        registerLocalBroadCastReceiver()
        initFlow()
        splashScreen = installSplashScreen().apply {
            setKeepOnScreenCondition {
                waiting
            }
        }
        //startSplash()
        super.onCreate(savedInstanceState)

        binding.loginToolbar.apply {
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            this.navigationIcon = ContextCompat.getDrawable(this@LoginActivity, R.drawable.back)
        }
    }

    override fun onStart() {
        super.onStart()
        // activity의 toolbar에서 fragment간 navigation graph와 연동하기 위함, 이를 통해 toolbar의 뒤로가기 버튼을 눌렀을 때
        // fragment간 navigation을 관리하는 controller로 뒤로가기 함
        val navController = findNavController(binding.fragmentLogin.id)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.loginToolbar.setupWithNavController(navController, appBarConfiguration)

        // Navigation이랑 Toolbar랑 같이 쓰면 supportActionBar.setHomeAsUpIndicator()를 써도
        // HomeasUp Icon이 계속 적용이 안되고 초기화됨
        // 그래서 이 리스너로 계속해서 뒤로가기 버튼 커스텀된 것 적용
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            binding.loginToolbar.navigationIcon =
                ContextCompat.getDrawable(this@LoginActivity, R.drawable.back)
        }
    }

    private fun registerLocalBroadCastReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver, IntentFilter("loading")
        )
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.token.collect {
                    if (it != Token("", "")) {
                       login()
                    }
                    else{
                        waiting = false
                    }
                }
            }
        }

    }

    fun login() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        finish()

    }

    fun showAppBar(title: String) {
        binding.layoutAppbar.visibility = View.VISIBLE
        binding.loginToolbar.title = title
    }

    fun hideAppBar() {
        binding.layoutAppbar.visibility = View.GONE
    }


}