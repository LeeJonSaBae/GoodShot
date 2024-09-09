package com.ijonsabae.presentation.main

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseActivity
import com.ijonsabae.presentation.databinding.ActivityLoginBinding
import com.ijonsabae.presentation.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.mainToolbar.apply {
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            this.navigationIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.back)
        }
        setContentView(binding.root)
        hideAppBar()
    }

    override fun onStart() {
        super.onStart()
        val navController = findNavController(binding.mainFragmentView.id)
        binding.navigation.setupWithNavController(navController)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.mainToolbar.setupWithNavController(navController, appBarConfiguration)

        // Navigation이랑 Toolbar랑 같이 쓰면 supportActionBar.setHomeAsUpIndicator()를 써도
        // HomeasUp Icon이 계속 적용이 안되고 초기화됨
        // 그래서 이 리스너로 계속해서 뒤로가기 버튼 커스텀된 것 적용
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            binding.mainToolbar.navigationIcon =
                ContextCompat.getDrawable(this@MainActivity, R.drawable.back)
        }
//        binding.navigation.setOnItemSelectedListener { item ->
//            when(item.itemId){
//                R.id.home -> {
//                    navController.navigate(R.id.home)
//                    true
//                }
//                R.id.shot -> {
//
//                }
//                R.id.replay -> {
//
//                }
//                R.id.profile -> {
//
//                }
//                else -> false
//            }
//        }
    }

    fun showAppBar(title: String){
        binding.layoutMainAppbar.visibility = View.VISIBLE
        binding.mainToolbar.title = title
    }
    fun hideAppBar(){
        binding.layoutMainAppbar.visibility = View.GONE
    }
}