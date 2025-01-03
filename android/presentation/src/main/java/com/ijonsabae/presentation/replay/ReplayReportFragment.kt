package com.ijonsabae.presentation.replay

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.config.Const.Companion.BACKSWING
import com.ijonsabae.presentation.config.Const.Companion.DOWNSWING
import com.ijonsabae.presentation.databinding.FragmentReplayReportBinding
import com.ijonsabae.presentation.shot.SwingLocalDataProcessor
import kotlin.math.abs

private const val TAG = "굿샷_ReplayReportFragment"

class ReplayReportFragment :
    BaseFragment<FragmentReplayReportBinding>(
        FragmentReplayReportBinding::bind,
        R.layout.fragment_replay_report
    ) {
    private val args: ReplayReportFragmentArgs by navArgs()
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private val swingFlowAdapter by lazy { SwingFlowAdapter() }
    private val REPLAY_REPORT_MARGIN_PX by lazy { resources.getDimension(R.dimen.replay_report_margin_dp_between_items) }
    private val backSwingFlowAnalysisAdapter by lazy { BackSwingFlowAnalysisAdapter() }
    private val downSwingFlowAnalysisAdapter by lazy { DownSwingFlowAnalysisAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initReplayVideo()
        initSwingFlowViewPager(binding.vpSwingFlow)
        initSummary()
        initSwingFlowAnalysisRecyclerView()
    }

    private fun initReplayVideo() {
        playerView = binding.pvReplayVideo
        player = ExoPlayer.Builder(requireContext()).build()
        playerView.player = player
        val videoUri = Uri.parse(SwingLocalDataProcessor.getSwingVideoFile(fragmentContext, swingCode = args.SwingFeedback.swingCode, userId = args.SwingFeedback.userID).toString())
        val mediaItem = MediaItem.fromUri(videoUri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = false // 처음에 정지 상태

        // 비디오 비율 설정
        player.addListener(object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                val videoWidth = videoSize.width
                val videoHeight = videoSize.height
                setCardViewAspectRatio(videoWidth, videoHeight)
            }
        })
    }

    private fun setCardViewAspectRatio(videoWidth: Int, videoHeight: Int) {
        val constraintLayout = activity?.findViewById<ConstraintLayout>(R.id.root_layout)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.setDimensionRatio(binding.cvReplayVideo.id, "$videoWidth:$videoHeight")
        constraintSet.applyTo(constraintLayout)
    }

    private fun initSwingFlowViewPager(viewPager: ViewPager2) {
        viewPager.adapter = swingFlowAdapter
        swingFlowAdapter.submitList(loadPoseImage())
        viewPager.offscreenPageLimit = 2
        viewPager.overScrollMode = RecyclerView.OVER_SCROLL_NEVER


        val screenWidth = resources.displayMetrics.widthPixels
        val marginPx =
            (screenWidth * 0.05).toInt() + (REPLAY_REPORT_MARGIN_PX * 2).toInt()

        val itemDecoration = HorizontalMarginItemDecoration(horizontalMarginInPx = marginPx)
        viewPager.addItemDecoration(itemDecoration)
        viewPager.setPageTransformer { page, position ->
            val offsetX =  position * -(2.7 * marginPx).toInt()
            page.translationX = offsetX

            val scale = 1 - abs(position)
            page.scaleY = 0.85f + 0.15f * scale
        }
    }

    private fun initSummary() {
        binding.tvSummary.text = args.SwingFeedback.solution
    }

    private fun loadPoseImage(): List<SwingFlowDTO>{
        val result = SwingLocalDataProcessor.getSwingPoseFiles(fragmentContext, swingCode = args.SwingFeedback.swingCode, userId = args.SwingFeedback.userID)
        val pose = listOf("ADDRESS", "TOE_UP", "MID_BACKSWING", "TOP", "MID_DOWNSWING", "IMPACT", "MID_FOLLOW_THROUGH", "FINISH")
        return result.mapIndexed { index, file ->
            SwingFlowDTO(
                title = pose[index],
                swingImg = file.toUri()
            ,)
        }
    }


    private fun initSwingFlowAnalysisRecyclerView() {
        val backSwingFlowAnalysisRecyclerView = binding.rvBackSwingFlowAnalysis
        backSwingFlowAnalysisRecyclerView.layoutManager = LinearLayoutManager(context)
        backSwingFlowAnalysisRecyclerView.adapter = backSwingFlowAnalysisAdapter

        val downSwingCommentList = args.SwingFeedbackCommentList.filter{
            // poseType이 downSwing인 것만
            it.poseType == DOWNSWING
        }.toMutableList()

        val backSwingCommentList = args.SwingFeedbackCommentList.filter{
            // poseType이 backSwing인 것만
            it.poseType == BACKSWING
        }.toMutableList()

        backSwingFlowAnalysisAdapter.submitList(downSwingCommentList)

        val downSwingFlowAnalysisRecyclerView = binding.rvDownSwingFlowAnalysis
        downSwingFlowAnalysisRecyclerView.layoutManager = LinearLayoutManager(context)
        downSwingFlowAnalysisRecyclerView.adapter = downSwingFlowAnalysisAdapter

        downSwingFlowAnalysisAdapter.submitList(backSwingCommentList)
    }

    override fun onStart() {
        super.onStart()
        if (!this::player.isInitialized) {
            player = ExoPlayer.Builder(requireContext()).build()
            playerView.player = player
        }
        player.playWhenReady = false
    }

    override fun onStop() {
        super.onStop()
        player.playWhenReady = false
        player.release()
    }


}