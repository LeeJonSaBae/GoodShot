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
    private val swingFlowAdapter by lazy { SwingFlowAdapter() }
    private val swingFlowAnalysisAdapter by lazy { SwingFlowAnalysisAdapter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initReplayVideo()
        initSwingFlowViewPagerAndRecyclerView(binding.vpSwingFlow)
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

    private fun initSwingFlowViewPagerAndRecyclerView(viewPager: ViewPager2) {
        swingFlowAdapter.apply { submitList(getSwingFlowData()) }
        viewPager.adapter = swingFlowAdapter

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

        initSwingFlowAnalysisRecyclerView()
    }


    private fun initSwingFlowAnalysisRecyclerView() {
        val swingFlowAnalysisRecyclerView = binding.rvSwingFlowAnalysis
        swingFlowAnalysisRecyclerView.layoutManager = LinearLayoutManager(context)
        swingFlowAnalysisRecyclerView.adapter = swingFlowAnalysisAdapter

        val initialList = getSwingFlowAnalysisData(0)
        swingFlowAnalysisAdapter.submitList(initialList)

        // ViewPager와 RecyclerView 내용 연동
        binding.vpSwingFlow.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val newItems = getSwingFlowAnalysisData(position)
                swingFlowAnalysisAdapter.updateData(newItems)
            }
        })
    }

    private fun getSwingFlowData(): List<SwingFlowDTO> {
        // TODO : api 연결 후 데이터 가져와서 뿌리도록 바꾸기 (지금은 dummy data)
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

    private fun getSwingFlowAnalysisData(position: Int): MutableList<SwingFlowAnalysisDTO> {
        return when (position) {
            // TODO : api 연결 후 여기 데이터 가져와서 뿌리도록 바꾸기 (지금은 dummy data)
            0 -> mutableListOf(
                SwingFlowAnalysisDTO(
                    true, "골판 위치가 수평으로 잘 유지됐어요!"
                ),
                SwingFlowAnalysisDTO(
                    true, "어깨가 한쪽으로 기울어지지 않도록 해야 합니다."
                ),
                SwingFlowAnalysisDTO(
                    false, "허리가 꺾여 있어요 주의해야 할 것 같아요."
                ),
                SwingFlowAnalysisDTO(
                    false, "머리 위치가 중앙에 고정되어 있어요!"
                ),
                SwingFlowAnalysisDTO(
                    true, "골반이 과하게 회전되어 있어요."
                )
            )

            1 -> mutableListOf(
                SwingFlowAnalysisDTO(
                    true, "아아아ㅏㅇ"
                ),
                SwingFlowAnalysisDTO(
                    false, "아ㅣ너랻ㄴ라ㅣ멀ㄴ!"
                ),
                SwingFlowAnalysisDTO(
                    true, "ㅁㄴㅇㄹ"
                ),
                SwingFlowAnalysisDTO(
                    true, "ㅈㄷㅅㄱㄷ"
                ),
                SwingFlowAnalysisDTO(
                    true, "ㅇ넘디;허ㅚ;ㄷ마/."
                )
            )

            else -> mutableListOf(
                SwingFlowAnalysisDTO(
                    false, "골판 위치가 수평으로 잘 유지됐어요!"
                ),
                SwingFlowAnalysisDTO(
                    true, "어깨가 한쪽으로 기울어지지 않도록 해야 합니다."
                ),
                SwingFlowAnalysisDTO(
                    true, "허리가 꺾여 있어요 주의해야 할 것 같아요."
                ),
                SwingFlowAnalysisDTO(
                    true, "머리 위치가 중앙에 고정되어 있어요!"
                ),
                SwingFlowAnalysisDTO(
                    false, "골반이 과하게 회전되어 있어요."
                )
            )
        }
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