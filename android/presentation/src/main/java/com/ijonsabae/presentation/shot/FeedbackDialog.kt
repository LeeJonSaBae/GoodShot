package com.ijonsabae.presentation.shot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseDialog
import com.ijonsabae.presentation.databinding.DialogFeedbackBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedbackDialog :
    BaseDialog<DialogFeedbackBinding>(DialogFeedbackBinding::bind, R.layout.dialog_feedback) {
    private val args: FeedbackDialogArgs by navArgs()
    private val checkListAdapter: CheckListAdapter by lazy {
        CheckListAdapter()
    }
    private val swingViewModel by activityViewModels<SwingViewModel>()

    private val skipMotionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == "SKIP_MOTION_DETECTED") {
                swingViewModel.setCurrentState(CameraState.ADDRESS)
                checkSwingCompletionAndNavigate()
                dismiss()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        registerLocalBroadCastReceiver()
        super.onViewCreated(view, savedInstanceState)
        initBackPress()
        initButtons()
        initRecyclerView()
        setFeedback()
    }

    override fun onStart() {
        super.onStart()
        setScreenWidthPercentage(0.9F)
        setScreenHeightPercentage(0.8F)
    }

    override fun onDestroyView() {
        swingViewModel.setCurrentState(CameraState.POSITIONING)
        super.onDestroyView()
    }

    private fun initButtons() {
        binding.btnClose.setOnClickListener {
            checkSwingCompletionAndNavigate()
            dismiss()
        }
    }

    private fun initBackPress() {
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                checkSwingCompletionAndNavigate()
                dismiss()
                true
            } else {
                false
            }
        }
    }

    private fun checkSwingCompletionAndNavigate() {
        if (args.swingCnt >= args.totalSwingCnt) {
            if (navController.currentDestination?.id == R.id.feedback_dialog) {
                navController.navigate(R.id.action_feedback_dialog_to_shot)
                showToastLong("스윙 촬영 횟수를 모두 채워 분석이 종료되었습니다.")
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvCheckList.adapter = checkListAdapter
        swingViewModel.getFeedBack()?.let {
            checkListAdapter.submitList(it.feedBackCheckList)
        }
    }

    private fun setFeedback() {
        swingViewModel.getFeedBack()?.apply {
            binding.apply {
                Glide.with(root)
                    .load(userSwingImage)
                    .into(ivMySwing)

                Glide.with(root)
                    .load(expertSwingImageResId)
                    .into(ivExpertSwing)

                tvTempo.text = tempo
                tvBack.text = back
                tvDown.text = down

                tvCheckListTitle.text = feedBackCheckListTitle
                tvFeedbackSolution.text = feedBackSolution
            }
        }

        binding.apply {
            "${args.swingCnt} / ${args.totalSwingCnt}".also { tvSwingCount.text = it }
        }
    }

    private fun registerLocalBroadCastReceiver() {
        LocalBroadcastManager.getInstance(fragmentContext).registerReceiver(
            skipMotionReceiver, IntentFilter("SKIP_MOTION_DETECTED")
        )
    }

    private fun PlayerView.setPlayer(uri: String) {
        val videoUri = Uri.parse(uri)
        val mediaItem = MediaItem.fromUri(videoUri)
        this.apply {
            useController = false
            player = ExoPlayer.Builder(fragmentContext).build().apply {
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true // 처음에 정지 상태
                repeatMode = Player.REPEAT_MODE_ALL
            }
        }

    }
}
