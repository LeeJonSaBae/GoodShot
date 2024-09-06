package com.ijonsabae.presentation.login

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentFindPasswordBinding
import com.ijonsabae.presentation.databinding.FragmentLoginBinding

class FindPasswordFragment : BaseFragment<FragmentFindPasswordBinding>(FragmentFindPasswordBinding::bind, R.layout.fragment_find_password) {
    private lateinit var navController: NavController
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (fragmentContext as LoginActivity).showAppBar("비밀번호 찾기")
        navController = Navigation.findNavController(binding.root)

        binding.btnSendPassword.setOnClickListener {
            navController.navigate(R.id.action_find_password_to_login)
        }

    }
}