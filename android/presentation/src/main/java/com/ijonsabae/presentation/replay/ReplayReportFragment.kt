package com.ijonsabae.presentation.replay

import android.os.Bundle
import android.view.View
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentReplayReportBinding

class ReplayReportFragment :
    BaseFragment<FragmentReplayReportBinding>(
        FragmentReplayReportBinding::bind,
        R.layout.fragment_replay_report
    ) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
    }
}