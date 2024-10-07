package com.ijonsabae.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ijonsabae.domain.model.Token
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseDialog
import com.ijonsabae.presentation.databinding.DialogLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class LoginDialog : BaseDialog<DialogLoginBinding>(
    DialogLoginBinding::bind,
    R.layout.dialog_login
) {
    private val loginDialogViewModel: LoginDialogViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTextChangeListener()
        initClickListener()
        initFlow()
    }

    override fun onStart() {
        super.onStart()
        setScreenWidthPercentage(0.85F)
        setScreenHeightConstraint(WRAP_CONTENT)
    }

    private fun initClickListener(){
        binding.ivChangePasswordClose.setOnClickListener {
            dismiss()
        }

        binding.btnSubmit.setOnClickListener {
            if(checkValidation()){
                lifecycleScope.launch (coroutineExceptionHandler){
                    val result = loginDialogViewModel.login().getOrThrow()
                    runBlocking {
                        loginDialogViewModel.setToken(result.data)
                        loginDialogViewModel.saveToken(result.data)
                    }

                    if(binding.checkboxAutoLogin.isChecked){
                        loginDialogViewModel.setAutoLoginStatus(true)
                    }else{
                        loginDialogViewModel.setAutoLoginStatus(false)
                    }
                    dismiss()
                }
            }else{
                showToastShort("이메일과 패스워드를 제대로 입력해주세요!")
            }
        }
    }

    private fun initFlow(){
        lifecycleScope.launch(coroutineExceptionHandler) {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                loginDialogViewModel.token.collect{
                    if(it != Token.EMPTY){
                        profileViewModel.setLoginStatus(true)
                    }
                }
            }

        }
    }

    private fun initTextChangeListener() {
        binding.etEmail.doOnTextChanged { text, _, _, _ ->
            text.toString().let { inputText ->
                lifecycleScope.launch {
                    loginDialogViewModel.setEmail(inputText)
                }
                binding.textInputLayoutEmail.apply {
                    error = if (inputText.isBlank()) {
                        "이메일을 입력해주세요!"
                    } else {
                        isErrorEnabled = false
                        null
                    }
                }
            }
        }
        binding.etPassword.doOnTextChanged { text, _, _, _ ->
            text.toString().let { inputText ->
                lifecycleScope.launch {
                    loginDialogViewModel.setPassword(inputText)
                }
                binding.textInputLayoutPassword.apply {
                    error = if (inputText.isBlank()) {
                        "패스워드를 다시 입력해주세요!"
                    } else {
                        isErrorEnabled = false
                        null
                    }
                }
            }
        }
    }

    private fun checkValidation(): Boolean = loginDialogViewModel.checkValidation()
}