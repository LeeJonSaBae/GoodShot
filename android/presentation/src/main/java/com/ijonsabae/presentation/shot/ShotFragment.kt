package com.ijonsabae.presentation.shot

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentShotBinding
import com.ijonsabae.presentation.main.MainActivity

class ShotFragment :
    BaseFragment<FragmentShotBinding>(FragmentShotBinding::bind, R.layout.fragment_shot) {

    private val mDataList =
        arrayListOf("정답 스윙 자세 영상 시청", "카메라 세팅", "스윙 촬영 및 실시간 음성 피드백", "스윙 분석 결과 확인")
    private val PREFS_NAME = "GoodShotPrefs"
    private val FIRST_TIME_KEY = "first_time_key"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (fragmentContext as MainActivity).showAppBar("스윙 촬영")

        init()
        initFirstTutorial()
    }

    private fun init(){

        binding.cvBtnCamera.setOnClickListener {
            navController.navigate(R.id.action_shot_to_shot_option_dialog)
        }
        binding.btnCamera.setOnClickListener {
            navController.navigate(R.id.action_shot_to_shot_option_dialog)
        }

        binding.rvTimeline.apply {
            setOnTouchListener { _, _ -> true }
            layoutManager = LinearLayoutManager(fragmentContext)
            adapter = TimeLineAdapter(mDataList)
        }

        binding.btnTutorial.setOnClickListener {
            showTutorialDialog()
        }

        val anim = AnimationUtils.loadAnimation(requireActivity(), R.anim.blink)
        binding.tvCameraDescription.startAnimation(anim)
    }

    private fun initFirstTutorial(){
        val sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val isFirstTime = sharedPreferences.getBoolean(FIRST_TIME_KEY, true)

        if (isFirstTime) {
            showTutorialDialog()
            sharedPreferences.edit().putBoolean(FIRST_TIME_KEY, false).apply()
        }}

    private fun showTutorialDialog() {
        navController.navigate(R.id.action_shot_to_shot_tutorial_dialog)

    }
}