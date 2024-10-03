package com.ijonsabae.presentation.shot

import android.os.Bundle
import android.view.View
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseDialog
import com.ijonsabae.presentation.databinding.DialogShotTutorialBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShotTutorialDialog : BaseDialog<DialogShotTutorialBinding>(
    DialogShotTutorialBinding::bind,
    R.layout.dialog_shot_tutorial
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }

    override fun onStart() {
        super.onStart()
        setScreenWidthPercentage(0.8F)
        setScreenHeightPercentage(0.7F)
    }
}