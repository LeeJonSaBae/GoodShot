package com.ijonsabae.presentation.replay

import android.os.Bundle
import android.view.View
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentReplayBinding
import com.ijonsabae.presentation.main.MainActivity

class ReplayFragment: BaseFragment<FragmentReplayBinding>(FragmentReplayBinding::bind, R.layout.fragment_replay) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (fragmentContext as MainActivity).showAppBar("영상 다시보기")
    }


}