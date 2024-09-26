package com.ijonsabae.presentation.shot

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.databinding.DialogShotBinding
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowOrientationRules
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonHighlightAnimation
import com.skydoves.balloon.BalloonSizeSpec

class ShotDialog : DialogFragment() {

    private var _binding: DialogShotBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogShotBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
        navController = findNavController()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        initTooltip()
    }

    private fun initButtons() {
        binding.btnOk.setOnClickListener {
            saveResult()
            navController.navigate(R.id.action_shot_dialog_to_swing_example)
        }
        binding.sbShotCnt.addOnChangeListener { slider, value, fromUser ->
            binding.tvSliderValue.text = "${value.toInt()} 회"
        }

        with(binding) {
            btnDirectionLeft.setOnClickListener {
                selectButton(btnDirectionLeft)
                deselectButton(btnDirectionRight)
            }
            btnDirectionRight.setOnClickListener {
                selectButton(btnDirectionRight)
                deselectButton(btnDirectionLeft)
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


        binding.ivHelp.setOnClickListener {
            balloon1.showAlignTop(it)
        }
        binding.ivHelp2.setOnClickListener {
            balloon2.showAlignTop(it)
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


    override fun onStart() {
        super.onStart()
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = getScreenWidth(this.requireContext())

        val newWidth = (screenWidth * 0.8).toInt()
        val layoutParams = requireView().layoutParams
        layoutParams.width = newWidth
        requireView().layoutParams = layoutParams
    }

    private fun getScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = wm.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            wm.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }
}