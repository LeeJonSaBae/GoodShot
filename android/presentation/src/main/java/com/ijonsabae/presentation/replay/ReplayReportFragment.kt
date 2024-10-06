package com.ijonsabae.presentation.replay

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
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
import com.ijonsabae.presentation.databinding.FragmentReplayReportBinding
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
        val videoUri = Uri.parse("android.resource://${activity?.packageName}/${R.raw.test_video}")
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

        viewPager.offscreenPageLimit = 1
        viewPager.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val itemDecoration = HorizontalMarginItemDecoration(horizontalMarginInPx = 20)
        viewPager.addItemDecoration(itemDecoration)
        viewPager.setPageTransformer { page, position ->
            val offsetX = position * -(2 * 150) // offset 값으로 간격 조정
            page.translationX = offsetX

            val scale = 1 - abs(position) // scale 값으로 양쪽 애들 높이 조정
            page.scaleY = 0.85f + 0.15f * scale
        }
    }

    private fun initSummary() {
        args
        binding.tvSummary.text = "탑스윙까지 팔꿈치를 쭉 펴고 올려보세요.\n더 높은 파워로 일정한 스윙을 할 수 있을 거에요."
    }


    private fun initSwingFlowAnalysisRecyclerView() {
        val backSwingFlowAnalysisRecyclerView = binding.rvBackSwingFlowAnalysis
        backSwingFlowAnalysisRecyclerView.layoutManager = LinearLayoutManager(context)
        backSwingFlowAnalysisRecyclerView.adapter = backSwingFlowAnalysisAdapter

        val downSwingCommentList = args.SwingFeedbackCommentList.filter{
            // poseType이 downSwing인 것만
            it.poseType == 1
        }.toMutableList()

        val backSwingCommentList = args.SwingFeedbackCommentList.filter{
            // poseType이 backSwing인 것만
            it.poseType == 2
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