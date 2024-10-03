package com.ijonsabae.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.lifecycle.lifecycleScope
import com.ijonsabae.domain.usecase.login.ClearLocalTokenUseCase
import com.ijonsabae.domain.usecase.profile.ResignUseCase
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseDialog
import com.ijonsabae.presentation.databinding.DialogResignBinding
import com.ijonsabae.presentation.login.LoginActivity
import com.ijonsabae.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ResignDialog:
    BaseDialog<DialogResignBinding>(DialogResignBinding::bind, R.layout.dialog_resign) {
        @Inject
    lateinit var resignUseCase: ResignUseCase
    @Inject
    lateinit var clearLocalTokenUseCase: ClearLocalTokenUseCase
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()
        setScreenWidthPercentage(0.9F)
        setScreenHeightConstraint(WRAP_CONTENT)
    }

    private fun initClickListener() {
        binding.apply {
            btnClose.setOnClickListener { dismiss() }
            btnNo.setOnClickListener { dismiss() }
            btnYes.setOnClickListener {
                lifecycleScope.launch {
                    resignUseCase().getOrThrow()
                    clearLocalTokenUseCase()
                    showToastShort("탈퇴되었습니다!")
                    val intent = Intent(fragmentContext, LoginActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                    dismiss()
                    (fragmentContext as MainActivity).finish()
                }
            }
        }
    }
}