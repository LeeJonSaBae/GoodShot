package com.ijonsabae.presentation.login

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.view.View.NOT_FOCUSABLE
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(
    FragmentRegisterBinding::bind, R.layout.fragment_register
) {
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUnderLineText()
        initTextChangeListener()
        initClickListener()
        initFlow()
        (fragmentContext as LoginActivity).showAppBar("회원가입")
    }

    private fun initUnderLineText() {
        val underlineString =
            SpannableString("약관보기").apply { setSpan(UnderlineSpan(), 0, "약관보기".length, 0) }
        binding.policy1Detail.text = underlineString
        binding.policy2Detail.text = underlineString
    }

    private fun initClickListener() {
        binding.btnRegister.setOnClickListener {
            if(checkValidation()){
                lifecycleScope.launch {
                    registerViewModel.setValidation(true)
                }
            }else{
                showToastShort("회원 가입에 필요한 정보 및 동의가 부족합니다!")
            }
        }

        binding.btnEmailAuth.setOnClickListener {
            lifecycleScope.launch(coroutineExceptionHandler) {
                registerViewModel.requestAuthCode().getOrThrow()
                showToastShort("이메일로 인증 코드가 전송되었습니다!")
                showToastShort("5분 내로 입력해주세요!")
            }
        }

        binding.btnVerifyAuth.setOnClickListener {
            lifecycleScope.launch(coroutineExceptionHandler) {
                val result = registerViewModel.verifyAuthCode().getOrThrow()
                if(result.data.checkCode){
                    // 이메일하고 이메일 인증 창 입력 막음
                    binding.btnVerifyAuth.visibility = View.INVISIBLE
                    binding.btnEmailAuth.visibility = View.INVISIBLE
                    binding.tilEmailVerify.focusable = NOT_FOCUSABLE
                    binding.tilEmailVerify.isEnabled = false
                    binding.tilEmail.focusable = NOT_FOCUSABLE
                    binding.tilEmail.isEnabled = false
                    binding.tilEmailVerify.boxBackgroundColor = ContextCompat.getColor(fragmentContext,R.color.gray)
                    binding.tilEmail.boxBackgroundColor = ContextCompat.getColor(fragmentContext,R.color.gray)
                    showToastShort("이메일 인증에 성공했습니다!")
                    registerViewModel.setAuthCheckedStatus(true)
                }
            }
        }

        binding.btnCheckPolicy1.setOnClickListener {
            if (!registerViewModel.policy1Confirmed.value) {
                lifecycleScope.launch {
                    registerViewModel.setPolicy1ConfirmStatus(true)
                    it.backgroundTintList =
                        ContextCompat.getColorStateList(fragmentContext, R.color.dark_green)
                }
            } else {
                lifecycleScope.launch {
                    registerViewModel.setPolicy1ConfirmStatus(false)
                    it.backgroundTintList =
                        ContextCompat.getColorStateList(fragmentContext, android.R.color.darker_gray)
                }
            }
        }

        binding.btnCheckPolicy2.setOnClickListener {
            if (!registerViewModel.policy2Confirmed.value) {
                lifecycleScope.launch {
                    registerViewModel.setPolicy2ConfirmStatus(true)
                    it.backgroundTintList =
                        ContextCompat.getColorStateList(fragmentContext, R.color.dark_green)
                }
            } else {
                lifecycleScope.launch {
                    registerViewModel.setPolicy2ConfirmStatus(false)
                    it.backgroundTintList =
                        ContextCompat.getColorStateList(fragmentContext, android.R.color.darker_gray)
                }
            }
        }
    }

    private fun initTextChangeListener() {
        binding.tieName.doOnTextChanged { text, _, _, _ ->
            text.toString().let { inputText ->
                lifecycleScope.launch {
                    registerViewModel.setName(inputText)
                }
                binding.tilName.apply {
                    error = if (inputText.isBlank()) {
                        "이름을 입력해주세요!"
                    } else {
                        isErrorEnabled = false
                        null
                    }
                }
            }
        }

        binding.tieEmail.doOnTextChanged { text, _, _, _ ->
            text.toString().let { inputText ->
                lifecycleScope.launch {
                    registerViewModel.setEmail(inputText)
                }
                binding.tilEmail.apply {
                    error = if (inputText.isBlank()) {
                        "패스워드를 입력해주세요!"
                    } else {
                        isErrorEnabled = false
                        null
                    }
                }
            }
        }

        binding.tieEmailVerify.doOnTextChanged { text, _, _, _ ->
            text.toString().let { inputText ->
                lifecycleScope.launch {
                    registerViewModel.setAuthCode(inputText)
                }
            }
        }

        binding.tiePassword.doOnTextChanged { text, start, before, count ->
            text.toString().let { inputText ->
                lifecycleScope.launch {
                    registerViewModel.setPassword(inputText)
                }
                binding.tilPassword.apply {
                    error = if (inputText.isBlank()) {
                        "패스워드를 입력해주세요!"
                    } else {
                        isErrorEnabled = false
                        null
                    }
                }
            }
        }

        binding.tiePasswordRepeat.doOnTextChanged { text, start, before, count ->
            text.toString().let { inputText ->
                lifecycleScope.launch {
                    registerViewModel.setPasswordRepeat(inputText)
                }
                binding.tilPasswordRepeat.apply {
                    error = if (inputText.isBlank()) {
                        "패스워드를 입력해주세요!"
                    } else {
                        isErrorEnabled = false
                        null
                    }
                }
            }
        }
    }

    private fun initFlow(){
        lifecycleScope.launch(coroutineExceptionHandler) {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    registerViewModel.validation.collect{pass ->
                        if(pass){
                            registerViewModel.join().getOrThrow()
                            registerViewModel.setJoinCompletedStatus(true)
                        }
                    }
                }
                launch {
                    registerViewModel.joinCompleted.collect{ completed ->
                        if(completed){
                            showToastShort("회원가입에 성공했습니다!")
                            navController.navigate(R.id.action_register_to_login)
                        }
                    }
                }
            }
        }
    }

    private fun checkValidation(): Boolean {
        return registerViewModel.let {
            !(it.name.value.isBlank() || it.password.value.isBlank() || it.passwordRepeat.value.isBlank() || it.password.value != it.passwordRepeat.value || !it.authChecked.value || !it.policy1Confirmed.value || !it.policy2Confirmed.value)
        }
    }
}