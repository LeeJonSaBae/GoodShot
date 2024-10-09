package com.ijonsabae.presentation.main

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseActivity
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
        hideAppBar()
    }

    override fun onStart() {
        super.onStart()
        val navController = findNavController(binding.mainFragmentView.id)
        binding.navigation.setupWithNavController(navController)
//        binding.navigation.setOnItemSelectedListener {item ->
//            val navOptions = NavOptions.Builder()
//                .setPopUpTo(navController.graph.startDestinationId, true) // 백스택을 초기화
//                .build()
//            when(item.itemId){
//                R.id.home_tab -> {
//                    navController.navigate(R.id.home, null, navOptions)
//                    true
//                }
//                R.id.shot_tab -> {
//                    navController.navigate(R.id.shot, null, navOptions)
//                    true
//                }
//                R.id.replay_tab -> {
//                    navController.navigate(R.id.replay, null, navOptions)
//                    true
//                }
//                R.id.profile_tab -> {
//                    navController.navigate(R.id.profile, null, navOptions)
//                    true
//                }
//                else -> {
//                    false
//                }
//            }
//        }
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.mainToolbar.setupWithNavController(navController, appBarConfiguration)

        // Navigation이랑 Toolbar랑 같이 쓰면 supportActionBar.setHomeAsUpIndicator()를 써도
        // HomeasUp Icon이 계속 적용이 안되고 초기화됨
        // 그래서 이 리스너로 계속해서 뒤로가기 버튼 커스텀된 것 적용
        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            binding.mainToolbar.navigationIcon =
                ContextCompat.getDrawable(this@MainActivity, R.drawable.back)
        }
    }

    fun showAppBar(title: String){
        binding.layoutMainAppbar.visibility = View.VISIBLE
        binding.mainToolbar.title = title
    }
    fun hideAppBar(){
        binding.layoutMainAppbar.visibility = View.GONE
    }

    fun showBottomNavBar(){
        binding.navigation.visibility = View.VISIBLE
    }
    fun hideBottomNavBar(){
        binding.navigation.visibility = View.GONE
    }

    fun changeBottomNavbarSelectedItemId(id: Int){
        binding.navigation.selectedItemId = id
    }

}