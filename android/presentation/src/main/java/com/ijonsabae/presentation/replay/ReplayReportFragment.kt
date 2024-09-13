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

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initReplayVideo()
        initSwingFlowViewPager(binding.vpSwingFlow)
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
        val adapter = SwingFlowAdapter().apply { submitList(getSwingFlowData()) }
        viewPager.adapter = adapter

        viewPager.offscreenPageLimit = 1
        viewPager.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val itemDecoration = HorizontalMarginItemDecoration(horizontalMarginInDp = 20)
        viewPager.addItemDecoration(itemDecoration)
        viewPager.setPageTransformer { page, position ->
            val offsetX = position * -(2 * 150) // offset 값으로 간격 조정
            page.translationX = offsetX

            val scale = 1 - abs(position) // scale 값으로 양쪽 애들 높이 조정
            page.scaleY = 0.85f + 0.15f * scale
        }
    }

    private fun initSwingFlowAnalysisRecyclerView() {
        val swingFlowAnalysisRecyclerView = binding.rvSwingFlowAnalysis
        val swingFlowAnalysisAdapter = SwingFlowAnalysisAdapter()
        swingFlowAnalysisRecyclerView.layoutManager = LinearLayoutManager(context)
        swingFlowAnalysisRecyclerView.adapter = swingFlowAnalysisAdapter

        swingFlowAnalysisAdapter.submitList(getSwingFlowAnalysisData()) // 데이터 갱신

        // TODO : 페이지에 따라 내용 연동시키기
//        binding.rvSwingFlowAnalysis.registerOnPageChangeCallback(object :
//            ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                // ViewPager의 현재 위치에 맞게 RecyclerView의 데이터를 변경
//                val newItems = getRecyclerViewItems(position) // 해당 페이지에 맞는 데이터 가져오기
//                recyclerViewAdapter.updateData(newItems)
//            }
//        })
    }

    private fun getSwingFlowData(): List<SwingFlowDTO> {
        return listOf(
            SwingFlowDTO("Address", resources.getDrawable(R.drawable.dummy_img)),
            SwingFlowDTO("Toe-Up", resources.getDrawable(R.drawable.dummy_img)),
            SwingFlowDTO("Mid-Backswing", resources.getDrawable(R.drawable.dummy_img)),
            SwingFlowDTO("Top", resources.getDrawable(R.drawable.dummy_img)),
            SwingFlowDTO("Mid-Downswing", resources.getDrawable(R.drawable.dummy_img)),
            SwingFlowDTO("Impact", resources.getDrawable(R.drawable.dummy_img)),
            SwingFlowDTO("Mid-Follow-Through", resources.getDrawable(R.drawable.dummy_img)),
            SwingFlowDTO("Finish", resources.getDrawable(R.drawable.dummy_img)),
        )
    }

    private fun getSwingFlowAnalysisData(): List<SwingFlowAnalysisDTO> {
        return listOf(
            SwingFlowAnalysisDTO(
                resources.getDrawable(R.drawable.ic_swing_report_unchecked),
                "골판 위치가 수평으로 잘 유지됐어요!"
            ),
            SwingFlowAnalysisDTO(
                resources.getDrawable(R.drawable.ic_swing_report_checked),
                "어깨가 한쪽으로 기울어지지 않도록 해야 합니다."
            ),
            SwingFlowAnalysisDTO(
                resources.getDrawable(R.drawable.ic_swing_report_unchecked),
                "머리 위치가 중앙에 고정되어 있어요!"
            ),
            SwingFlowAnalysisDTO(
                resources.getDrawable(R.drawable.ic_swing_report_checked),
                "골반이 과하게 회전되어 있어요."
            ),
            SwingFlowAnalysisDTO(
                resources.getDrawable(R.drawable.ic_swing_report_checked),
                "허리가 꺾여 있어요 주의해야 할 것 같아요."
            ),

            )
    }

    override fun onStop() {
        super.onStop()
        player.playWhenReady = false
        player.release()
    }

    override fun onStart() {
        super.onStart()
        if (!this::player.isInitialized) {
            player = ExoPlayer.Builder(requireContext()).build()
            playerView.player = player
        }
        player.playWhenReady = false
    }

}