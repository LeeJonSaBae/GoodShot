package com.ijonsabae.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseDialog
import com.ijonsabae.presentation.databinding.DialogChangePasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordDialog : BaseDialog<DialogChangePasswordBinding>(
    DialogChangePasswordBinding::bind,
    R.layout.dialog_change_password
) {
    private val changePasswordViewModel: ChangePasswordDialogViewModel by viewModels()
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
                    changePasswordViewModel.changePassword().getOrThrow()
                    showToastShort("변경 완료!")
                    dismiss()
                }
            }else{
                showToastShort("제대로 비밀번호를 입력해주세요!")
            }

        }
    }

    private fun initTextChangeListener() {
        binding.etCurrentPassword.doOnTextChanged { text, _, _, _ ->
            text.toString().let { inputText ->
                lifecycleScope.launch {
                    changePasswordViewModel.setOldPassword(inputText)
                }
                binding.textInputLayoutCurrentPassword.apply {
                    error = if (inputText.isBlank()) {
                        "현재 패스워드를 입력해주세요!"
                    } else {
                        isErrorEnabled = false
                        null
                    }
                }
            }
        }
        binding.etCheckPassword.doOnTextChanged { text, _, _, _ ->
            text.toString().let { inputText ->
                lifecycleScope.launch {
                    changePasswordViewModel.setNewPasswordRepeat(inputText)
                }
                binding.textInputLayoutCheckPassword.apply {
                    error = if (inputText.isBlank()) {
                        "패스워드를 다시 입력해주세요!"
                    } else {
                        isErrorEnabled = false
                        null
                    }
                }
            }
        }
        binding.etNewPassword.doOnTextChanged { text, _, _, _ ->
            text.toString().let { inputText ->
                lifecycleScope.launch {
                    changePasswordViewModel.setNewPassword(inputText)
                }
                binding.textInputLayoutNewPassword.apply {
                    error = if (inputText.isBlank()) {
                        "변경할 패스워드를 입력해주세요!"
                    } else {
                        isErrorEnabled = false
                        null
                    }
                }
            }
        }
    }

    private fun checkValidation(): Boolean = changePasswordViewModel.checkValidation()
}