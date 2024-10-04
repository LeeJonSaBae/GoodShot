package com.ijonsabae.presentation.shot

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseDialog
import com.ijonsabae.presentation.databinding.DialogShotOptionBinding
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowOrientationRules
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonHighlightAnimation
import com.skydoves.balloon.BalloonSizeSpec
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "ShotDialog 싸피"

@AndroidEntryPoint
class ShotDialog : BaseDialog<DialogShotOptionBinding>(
    DialogShotOptionBinding::bind,
    R.layout.dialog_shot_option
) {
    private val shotDialogViewModel: ShotSettingViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
        initFlow()
        initButtons()
        initTooltip()
    }

    private fun initButtons() {
        binding.sbShotCnt.addOnChangeListener { slider, value, fromUser ->
            lifecycleScope.launch {
                shotDialogViewModel.setTotalSwingCnt(value.toInt())
            }
        }

        with(binding) {
            btnOk.setOnClickListener {
                saveResult()
                if (tbShowAnswer.isChecked) {
                    navController.navigate(R.id.action_shot_dialog_to_swing_example)
                } else {
                    navController.navigate(R.id.action_shot_dialog_to_camera)
                }
            }
            btnDirectionLeft.setOnClickListener {
                lifecycleScope.launch {
                    shotDialogViewModel.setIsLeftStatus(true)
                }
            }
            btnDirectionRight.setOnClickListener {
                lifecycleScope.launch {
                    shotDialogViewModel.setIsLeftStatus(false)
                }
            }
            btnSwingPoseFront.setOnClickListener {
                selectButton(btnSwingPoseFront)
                deselectButton(btnSwingPoseSide)
            }
            btnSwingPoseSide.setOnClickListener {
                selectButton(btnSwingPoseSide)
                deselectButton(btnSwingPoseFront)
            }
            btnGolfClubIron.setOnClickListener {
                selectButton(btnGolfClubIron)
                deselectButton(btnGolfClubDriver)
            }
            btnGolfClubDriver.setOnClickListener {
                selectButton(btnGolfClubDriver)
                deselectButton(btnGolfClubIron)
            }
//            tbPoseReport.setOnClickListener {
//                lifecycleScope.launch {
//                    setShowPoseReportStatus(tbPoseReport.isChecked)
//                }
//            }
        }
    }

    private fun initTooltip() {
        val balloon1 = Balloon.Builder(requireContext())
            .setWidth(BalloonSizeSpec.WRAP)
            .setHeight(BalloonSizeSpec.WRAP)
            .setText("스윙할 때 마다\n템포 및 피드백을 받습니다.")
            .setTextColorResource(R.color.black)
            .setArrowOrientationRules(ArrowOrientationRules.ALIGN_FIXED)
            .setArrowOrientation(ArrowOrientation.BOTTOM)
            .setTextSize(11f)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowSize(10)
            .setArrowPosition(0.5f)
            .setPaddingVertical(8)
            .setPaddingHorizontal(15)
            .setCornerRadius(8f)
            .setBackgroundColorResource(R.color.chart_light_gray)
            .setBalloonAnimation(BalloonAnimation.FADE)
            .setBalloonHighlightAnimation(BalloonHighlightAnimation.SHAKE)
            .setLifecycleOwner(viewLifecycleOwner)
            .setAutoDismissDuration(3000L)
            .build()

        val balloon2 = Balloon.Builder(requireContext())
            .setWidth(BalloonSizeSpec.WRAP)
            .setHeight(BalloonSizeSpec.WRAP)
            .setText("스윙 촬영 전에\n프로의 모범 자세를 먼저 봅니다.")
            .setTextColorResource(R.color.black)
            .setArrowOrientationRules(ArrowOrientationRules.ALIGN_FIXED)
            .setArrowOrientation(ArrowOrientation.BOTTOM)
            .setTextSize(11f)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowSize(10)
            .setArrowPosition(0.5f)
            .setPaddingVertical(8)
            .setPaddingHorizontal(15)
            .setCornerRadius(8f)
            .setBackgroundColorResource(R.color.chart_light_gray)
            .setBalloonAnimation(BalloonAnimation.FADE)
            .setBalloonHighlightAnimation(BalloonHighlightAnimation.SHAKE)
            .setLifecycleOwner(viewLifecycleOwner)
            .setAutoDismissDuration(3000L)
            .build()


//        binding.ivHelp.setOnClickListener {
//            balloon1.showAlignTop(it)
//        }
        binding.ivHelp2.setOnClickListener {
            balloon2.showAlignTop(it)
        }

        lifecycleScope.launch {
            shotDialogViewModel.initializeTotalSwingCnt()
        }
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    shotDialogViewModel.isLeft.collect() {
                        if (it) {
                            selectButton(binding.btnDirectionLeft)
                            deselectButton(binding.btnDirectionRight)
                        } else {
                            selectButton(binding.btnDirectionRight)
                            deselectButton(binding.btnDirectionLeft)
                        }
                    }
                }
                launch {
                    shotDialogViewModel.totalSwingCnt.collect() { cnt ->
                        binding.tvSliderValue.text = "${cnt} 회"
                    }
                }
            }
        }
    }

    private fun selectButton(button: Button) {
        button.isSelected = true
        button.setTextColor(Color.WHITE)
    }

    private fun deselectButton(button: Button) {
        button.isSelected = false
        button.setTextColor(resources.getColor(R.color.dark_green))
    }

    private fun saveResult() {
        val selectedSwingPose = if (binding.btnSwingPoseFront.isSelected) "정면" else "측면"
        val selectedGolfClub = if (binding.btnGolfClubIron.isSelected) "아이언" else "드라이버"
        val selectedShotCnt = if (binding.btnGolfClubIron.isSelected) "아이언" else "드라이버"
    }

    private suspend fun setShowPoseReportStatus(status: Boolean) {
        shotDialogViewModel.setShowMidReportStatus(status)
    }


    override fun onStart() {
        super.onStart()
        setScreenWidthPercentage(0.8F)
    }
}