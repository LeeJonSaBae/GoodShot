package com.ijonsabae.presentation.shot

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.databinding.BottomSheetShotBinding

class ShotBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: BottomSheetShotBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetShotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomSheet()
        initButtons()

    }

    private fun initBottomSheet() {
        view?.viewTreeObserver?.addOnGlobalLayoutListener {
            val bottomSheet =
                dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
        binding.btnOk.setOnClickListener {
            saveResult()
            dismiss()
        }
        binding.sbShotCnt.addOnChangeListener { slider, value, fromUser ->
            binding.tvSliderValue.text = "${value.toInt()} 회"
        }
    }

    private fun initButtons() {
        with(binding) {
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
}