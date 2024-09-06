package com.ijonsabae.presentation.login

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::bind, R.layout.fragment_login) {
    private lateinit var navController: NavController
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFindPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.btnRegister.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        navController = Navigation.findNavController(binding.root)
        (fragmentContext as LoginActivity).hideAppBar()

        binding.btnRegister.setOnClickListener{
            navController.navigate(R.id.action_login_to_register)
        }
        binding.btnFindPassword.setOnClickListener {
            navController.navigate(R.id.action_login_to_find_password)
        }
    }
}