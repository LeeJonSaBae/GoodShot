package com.ijonsabae.presentation.login

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.ijonsabae.domain.model.LoginParam
import com.ijonsabae.domain.usecase.login.LoginUseCase
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment :
    BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::bind, R.layout.fragment_login) {
    private lateinit var navController: NavController

    @Inject
    lateinit var loginUseCase: LoginUseCase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val findPasswordDescription = "비밀번호를 잊어버리셨나요?"
        val registerDescription = "회원 가입"
        val underlinePasswordDescriptionString = SpannableString(findPasswordDescription).apply {
            setSpan(
                UnderlineSpan(),
                0,
                findPasswordDescription.length,
                0
            )
        }
        val underlineRegisterDescriptionString = SpannableString(registerDescription).apply {
            setSpan(
                UnderlineSpan(),
                0,
                registerDescription.length,
                0
            )
        }
        binding.btnFindPassword.text = underlinePasswordDescriptionString
        binding.btnRegister.text = underlineRegisterDescriptionString

        binding.etPassword.doOnTextChanged { text, start, before, count ->

        }

        (fragmentContext as LoginActivity).hideAppBar()
        navController = findNavController()

        binding.btnRegister.setOnClickListener {
            navController.navigate(R.id.action_login_to_register)
        }
        binding.btnFindPassword.setOnClickListener {
            navController.navigate(R.id.action_login_to_find_password)
        }
        binding.btnLogin.setOnClickListener {
            lifecycleScope.launch(coroutineExceptionHandler) {
                if (binding.etEmail.text.isNullOrBlank() || binding.etPassword.text.isNullOrBlank()) {
                    showToastShort("아이디와 패스워드를 입력해주세요!")
                } else {
                    val result = loginUseCase(
                        LoginParam(
                            binding.etEmail.text.toString(),
                            binding.etPassword.text.toString()
                        )
                    )
                    if (result.code == "201") {
                        val loginActivity = fragmentContext as LoginActivity
                        loginActivity.login()
                    } else {
                        showToastShort(result.message)
                    }
                }
            }
        }
    }
}