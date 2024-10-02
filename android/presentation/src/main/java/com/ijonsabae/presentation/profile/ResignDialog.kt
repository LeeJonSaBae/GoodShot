package com.ijonsabae.presentation.profile

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseDialog
import com.ijonsabae.presentation.databinding.DialogResignBinding

class ResignDialog: BaseDialog<DialogResignBinding>(DialogResignBinding::bind, R.layout.dialog_resign) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()
        setScreenWidthPercentage(0.9F)
        setScreenHeightConstraint(WRAP_CONTENT)
    }
    private fun initClickListener(){
        binding.apply {
            btnClose.setOnClickListener { dismiss() }
            btnNo.setOnClickListener { dismiss() }
            btnYes.setOnClickListener {
                // 탈퇴
                showToastShort("탈퇴되었습니다!")
                dismiss()
            }
        }
    }
}