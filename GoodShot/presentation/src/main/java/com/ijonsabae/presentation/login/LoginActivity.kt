package com.ijonsabae.presentation.login

import android.os.Bundle
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity: BaseActivity<LoginActivityBinding>(LoginActivity Binding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}