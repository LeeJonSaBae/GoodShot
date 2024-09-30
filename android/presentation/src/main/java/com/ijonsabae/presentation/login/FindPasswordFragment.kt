package com.ijonsabae.presentation.login

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentFindPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "FindPasswordFragment_싸피"
@AndroidEntryPoint
class FindPasswordFragment : BaseFragment<FragmentFindPasswordBinding>(FragmentFindPasswordBinding::bind, R.layout.fragment_find_password) {
    private val fpViewModel: FindPasswordViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showAppBar("비밀번호 찾기")
        initTextChangeListener()
        initClickListener()


    }

    private fun initClickListener(){
        binding.btnSendPassword.setOnClickListener {
            Log.d(TAG, "initClickListener: 클릭")
            if(checkValidation()){
                lifecycleScope.launch(coroutineExceptionHandler) {
                    fpViewModel.generateTemporaryPassword().getOrThrow()
                    navController.navigate(R.id.action_find_password_to_login)
                    showToastShort("임시 패스워드 발급에 성공했습니다!")
                    showToastShort("이메일을 확인해주세요!")
                }
            }else{
                showToastShort("이름과 이메일을 입력해주세요!")
            }
        }
    }

    private fun initTextChangeListener() {
        binding.etName.doOnTextChanged { text, _, _, _ ->
            text.toString().let { inputText ->
                lifecycleScope.launch {
                    fpViewModel.setName(inputText)
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

        binding.etEmail.doOnTextChanged { text, _, _, _ ->
            text.toString().let { inputText ->
                lifecycleScope.launch {
                    fpViewModel.setEmail(inputText)
                }
                binding.tilEmail.apply {
                    error = if (inputText.isBlank()) {
                        "이메일을 입력해주세요!"
                    } else {
                        isErrorEnabled = false
                        null
                    }
                }
            }
        }
    }

    private fun checkValidation(): Boolean {
        return fpViewModel.let {
            !(it.name.value.isBlank() || it.email.value.isBlank())
        }
    }

    private fun showAppBar(title: String){
        (fragmentContext as LoginActivity).showAppBar(title)
    }
}