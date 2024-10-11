package com.ijonsabae.presentation.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseDialog
import com.ijonsabae.presentation.databinding.DialogForbiddenBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ForbiddenDialog 싸피"
@AndroidEntryPoint
class ForbiddenDialog :
    BaseDialog<DialogForbiddenBinding>(DialogForbiddenBinding::bind, R.layout.dialog_forbidden) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenWidthPercentage(0.9F)
        setScreenHeightConstraint(WRAP_CONTENT)

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnYes.setOnClickListener {
            dismiss()
        }
    }

}