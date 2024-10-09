package com.ijonsabae.presentation.login

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ijonsabae.domain.model.LoginParam
import com.ijonsabae.domain.model.Token
import com.ijonsabae.domain.usecase.login.GetRemoteUserNameUseCase
import com.ijonsabae.domain.usecase.login.LoginUseCase
import com.ijonsabae.domain.usecase.login.SetLocalUserNameUseCase
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment :
    BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::bind, R.layout.fragment_login) {
    private val loginViewModel: LoginViewModel by activityViewModels()
    @Inject
    lateinit var getRemoteUserNameUseCase: GetRemoteUserNameUseCase
    @Inject
    lateinit var setLocalUserNameUseCase: SetLocalUserNameUseCase
    @Inject
    lateinit var loginUseCase: LoginUseCase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideAppBar()
        setUnderLineText()
        initClickListener()
    }

    private fun hideAppBar() {
        (fragmentContext as LoginActivity).hideAppBar()
    }

    private fun initClickListener() {
        binding.btnRegister.setOnClickListener {
            navController.navigate(R.id.action_login_to_register)
        }
        binding.btnFindPassword.setOnClickListener {
            navController.navigate(R.id.action_login_to_find_password)
        }
        binding.btnLogin.setOnClickListener {
            if (binding.etEmail.text.isNullOrBlank() || binding.etPassword.text.isNullOrBlank()) {
                showToastShort("아이디와 패스워드를 입력해주세요!")
            } else {
                lifecycleScope.launch(coroutineExceptionHandler) {
                    val result = loginUseCase(
                        LoginParam(
                            binding.etEmail.text.toString(),
                            binding.etPassword.text.toString()
                        )
                    ).getOrThrow()
                    withContext(Dispatchers.IO){
                        loginViewModel.saveToken(result.data)
                        setLocalUserNameUseCase(getRemoteUserNameUseCase().getOrThrow().data)
                        loginViewModel.setToken(result.data)
                    }
                    if(binding.checkbox.isChecked){
                        loginViewModel.setAutoLoginStatus(true)
                    }else{
                        loginViewModel.setAutoLoginStatus(false)
                    }
                }
            }
        }
        binding.btnGuestLogin.setOnClickListener{
            (fragmentContext as LoginActivity).login()
        }
    }

    private fun setUnderLineText() {
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
    }
}