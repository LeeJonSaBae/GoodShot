package com.ijonsabae.presentation.shot

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.fragment.navArgs
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseDialog
import com.ijonsabae.presentation.databinding.DialogFeedbackBinding

private const val TAG = "FeedbackDialog 싸피"
class FeedbackDialog :
    BaseDialog<DialogFeedbackBinding>(DialogFeedbackBinding::bind, R.layout.dialog_feedback) {
    private val args: FeedbackDialogArgs by navArgs()
    private val checkListAdapter: CheckListAdapter by lazy {
        CheckListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        initVideo()
        initRecyclerView()
        setArgs()
    }

    override fun onStart() {
        super.onStart()
        setScreenWidthPercentage(0.9F)
        setScreenHeightPercentage(0.8F)
    }

    private fun initButtons() {


    }

    private fun initVideo() {
        val myPlayerView = binding.pvReplayMyVideo
        val expertPlayerView = binding.pvReplayExpertVideo
        myPlayerView.setPlayer("android.resource://${activity?.packageName}/${R.raw.test_video}")
        expertPlayerView.setPlayer("android.resource://${activity?.packageName}/${R.raw.test_video}")
    }

    private fun initRecyclerView() {
        binding.rvCheckList.adapter = checkListAdapter
        checkListAdapter.submitList(args.feedback.feedBackCheckList)
    }

    private fun setArgs() {
        args.feedback.apply {
            binding.apply {
                tvTempo.text = tempo.toString()
                tvBack.text = back.toString()
                tvDown.text = down.toString()

                tvFeedbackSolution.text = feedBackSolution
                tvFeedbackProblem.text = feedBackProblem
            }
        }
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
