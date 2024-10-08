package com.ijonsabae.presentation.shot

import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseDialog
import com.ijonsabae.presentation.databinding.DialogShotTutorialBinding
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShotTutorialDialog : BaseDialog<DialogShotTutorialBinding>(
    DialogShotTutorialBinding::bind,
    R.layout.dialog_shot_tutorial
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        initViewPager()
    }

    private fun initViewPager() {
        val viewPager: ViewPager2 = binding.vpTutorial
        val dotsIndicator: SpringDotsIndicator = binding.vpIndicator

        val items = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val adapter = TutorialAdapter(items)

        viewPager.adapter = adapter
        dotsIndicator.setViewPager2(viewPager)
    }

    override fun onStart() {
        super.onStart()
        setScreenWidthPercentage(0.8F)
        setScreenHeightPercentage(0.6F)
    }
}