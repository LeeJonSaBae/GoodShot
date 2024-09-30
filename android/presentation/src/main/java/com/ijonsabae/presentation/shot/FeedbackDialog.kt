package com.ijonsabae.presentation.shot

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
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
        initRecyclerView()
        setArgs()
    }

    override fun onStart() {
        super.onStart()
        setScreenWidthPercentage(0.9F)
        setScreenHeightPercentage(0.8F)
    }

    private fun initButtons() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun initRecyclerView() {
        binding.rvCheckList.adapter = checkListAdapter
        checkListAdapter.submitList(args.feedback.feedBackCheckList)
    }

    private fun setArgs() {
        args.feedback.apply {
            binding.apply {
                Glide.with(root)
                    .load(R.drawable.swing_example)
                    .into(ivMySwing)

                Glide.with(root)
                    .load(R.drawable.swing_example)
                    .into(ivExpertSwing)

                tvTempo.text = tempo.toString()
                tvBack.text = back.toString()
                tvDown.text = down.toString()

                tvCheckListTitle.text = feedBackCheckListTitle
                tvFeedbackSolution.text = feedBackSolution
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
