package com.ijonsabae.presentation.login

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::bind, R.layout.fragment_login) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnForgotPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.btnRegister.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }
}