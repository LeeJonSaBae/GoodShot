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
import com.ijonsabae.domain.model.CommonResponse
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
        showAppBar("회원가입")
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
                setValidationTrue()
            }else{
                showToastShort("회원 가입에 필요한 정보 및 동의가 부족합니다!")
            }
        }

        binding.btnEmailAuth.setOnClickListener {
            lifecycleScope.launch(coroutineExceptionHandler) {
                if(emailIsNotDuplicated()){
                    requestAuthCode()
                    showToastShort("이메일로 인증 코드가 전송되었습니다!")
                    showToastShort("5분 내로 입력해주세요!")
                }else{
                    showToastShort("이미 존재하는 이메일입니다!")
                }
            }
        }

        binding.btnVerifyAuth.setOnClickListener {
            lifecycleScope.launch(coroutineExceptionHandler) {
                val result = getAuthCodeVerificationResult()
                if(result.data){
                    // 이메일하고 이메일 인증 창 입력 막음
                    setEmailLayerTilDisEnable()
                    showToastShort("이메일 인증에 성공했습니다!")
                    setAuthCheckStatusTrue()
                }
            }
        }

        binding.btnCheckPolicy1.setOnClickListener {
            lifecycleScope.launch {
                togglePolicy1ConfirmStatus()
            }
        }

        binding.btnCheckPolicy2.setOnClickListener {
            lifecycleScope.launch {
                togglePolicy2ConfirmStatus()
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
                    registerViewModel.policy1Confirmed.collect{ confirmed ->
                        if(confirmed){
                            lifecycleScope.launch {
                                registerViewModel.setPolicy1ConfirmStatus(true)
                                binding.btnCheckPolicy1.backgroundTintList =
                                    ContextCompat.getColorStateList(fragmentContext, R.color.dark_green)
                            }
                        }else {
                            lifecycleScope.launch {
                                registerViewModel.setPolicy1ConfirmStatus(false)
                                binding.btnCheckPolicy1.backgroundTintList =
                                    ContextCompat.getColorStateList(fragmentContext, android.R.color.darker_gray)
                            }
                        }
                    }
                }
                launch {
                    registerViewModel.policy2Confirmed.collect{ confirmed ->
                        if(confirmed){
                            lifecycleScope.launch {
                                registerViewModel.setPolicy2ConfirmStatus(true)
                                binding.btnCheckPolicy2.backgroundTintList =
                                    ContextCompat.getColorStateList(fragmentContext, R.color.dark_green)
                            }
                        }else {
                            lifecycleScope.launch {
                                registerViewModel.setPolicy2ConfirmStatus(false)
                                binding.btnCheckPolicy2.backgroundTintList =
                                    ContextCompat.getColorStateList(fragmentContext, android.R.color.darker_gray)
                            }
                        }
                    }
                }
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

    private fun showAppBar(title: String){
        (fragmentContext as LoginActivity).showAppBar(title)
    }

    private suspend fun togglePolicy1ConfirmStatus(){
        registerViewModel.setPolicy1ConfirmStatus(!registerViewModel.policy1Confirmed.value)
    }

    private suspend fun togglePolicy2ConfirmStatus(){
        registerViewModel.setPolicy2ConfirmStatus(!registerViewModel.policy2Confirmed.value)
    }

    private fun setEmailLayerTilDisEnable(){
        binding.btnVerifyAuth.visibility = View.INVISIBLE
        binding.btnEmailAuth.visibility = View.INVISIBLE
        binding.tilEmailVerify.focusable = NOT_FOCUSABLE
        binding.tilEmailVerify.isEnabled = false
        binding.tilEmail.focusable = NOT_FOCUSABLE
        binding.tilEmail.isEnabled = false
        binding.tilEmailVerify.boxBackgroundColor = ContextCompat.getColor(fragmentContext,R.color.gray)
        binding.tilEmail.boxBackgroundColor = ContextCompat.getColor(fragmentContext,R.color.gray)
    }

    private suspend fun getAuthCodeVerificationResult(): CommonResponse<Boolean> {
        return registerViewModel.verifyAuthCode().getOrThrow()
    }

    private suspend fun requestAuthCode(){
        registerViewModel.requestAuthCode().getOrThrow()
    }

    private suspend fun setAuthCheckStatusTrue(){
        registerViewModel.setAuthCheckedStatus(true)
    }

    private fun setValidationTrue(){
        lifecycleScope.launch {
            registerViewModel.setValidation(true)
        }
    }

    private fun checkValidation(): Boolean {
        return registerViewModel.let {
            !(it.name.value.isBlank() || it.password.value.isBlank() || it.passwordRepeat.value.isBlank() || it.password.value != it.passwordRepeat.value || !it.authChecked.value || !it.policy1Confirmed.value || !it.policy2Confirmed.value)
        }
    }

    private suspend fun emailIsNotDuplicated(): Boolean{
        return !registerViewModel.checkEmailDuplicated().getOrThrow().data
    }
}

