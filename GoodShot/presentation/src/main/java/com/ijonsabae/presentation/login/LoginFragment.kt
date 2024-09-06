package com.ijonsabae.presentation.login

import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
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
        val findPasswordDescription = "비밀번호를 잊어버리셨나요?"
        val registerDescription = "회원 가입"
        val underlinePasswordDescriptionString = SpannableString(findPasswordDescription).apply { setSpan(UnderlineSpan(),0, findPasswordDescription.length, 0) }
        val underlineRegisterDescriptionString = SpannableString(registerDescription).apply { setSpan(UnderlineSpan(),0, registerDescription.length, 0) }
        binding.btnFindPassword.text = underlinePasswordDescriptionString
        binding.btnRegister.text = underlineRegisterDescriptionString

        (fragmentContext as LoginActivity).hideAppBar()
        navController = Navigation.findNavController(binding.root)

        binding.btnRegister.setOnClickListener{
            navController.navigate(R.id.action_login_to_register)
        }
        binding.btnFindPassword.setOnClickListener {
            navController.navigate(R.id.action_login_to_find_password)
        }
    }
}