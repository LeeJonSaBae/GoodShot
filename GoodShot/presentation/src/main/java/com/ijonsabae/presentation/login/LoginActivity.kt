package com.ijonsabae.presentation.login

import android.os.Bundle
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseActivity
import com.ijonsabae.presentation.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity: BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}