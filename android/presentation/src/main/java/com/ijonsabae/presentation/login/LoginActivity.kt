package com.ijonsabae.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BarChartRender
import com.ijonsabae.presentation.config.BaseActivity
import com.ijonsabae.presentation.config.GolfSwingValueFormatter
import com.ijonsabae.presentation.databinding.ActivityLoginBinding
import com.ijonsabae.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


private const val TAG = "LoginActivity_싸피"
@AndroidEntryPoint
class LoginActivity: BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    private lateinit var splashScreen: SplashScreen
    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen().apply {
            setKeepOnScreenCondition{
                // 1초 지연
                runBlocking {
                    delay(1000)
                }
                false
            }
        }
        //startSplash()
        super.onCreate(savedInstanceState)
        binding.loginToolbar.apply {
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            this.navigationIcon = ContextCompat.getDrawable(this@LoginActivity, R.drawable.back)
        }
        setContentView(binding.root)
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

    fun login(){
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        finish()
    }
    fun showAppBar(title: String){
        binding.layoutAppbar.visibility = View.VISIBLE
        binding.loginToolbar.title = title
    }
    fun hideAppBar(){
        binding.layoutAppbar.visibility = View.GONE
    }


}