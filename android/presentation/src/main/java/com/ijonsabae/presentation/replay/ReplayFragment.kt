package com.ijonsabae.presentation.replay

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentReplayBinding
import com.ijonsabae.presentation.main.MainActivity

class ReplayFragment :
    BaseFragment<FragmentReplayBinding>(FragmentReplayBinding::bind, R.layout.fragment_replay) {

    private val replayAdapter by lazy {
        ReplayAdapter(requireContext()).apply {
            setItemClickListener(
                object : ReplayAdapter.OnItemClickListener {
                    override fun onItemClick(item: ReplayDTO) {
                        findNavController().navigate(R.id.action_replay_to_replayReport)
                    }

                    override fun onLikeClick(item: ReplayDTO, check: Boolean) {
                        Toast.makeText(context, "즐겨찾기 클릭~!", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (fragmentContext as MainActivity).showAppBar("영상 다시보기")
        initRecyclerView()
    }

    private fun initRecyclerView() {
        replayAdapter.submitList(getData())
        val mountainRecyclerView = binding.rvReplay
        mountainRecyclerView.layoutManager = LinearLayoutManager(context)
        mountainRecyclerView.adapter = replayAdapter

        // 스와이프로 삭제
        val swipeHelper = SwipeDeleteHelper().apply {
            setClamp(200f)
        }
        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(binding.rvReplay)

        // 여러 아이템이 한 번에 삭제 버튼이 보이는 경우 없도록 처리
        binding.rvReplay.apply {
            setOnTouchListener { v, event ->
                swipeHelper.removePreviousClamp(this)
                false
            }
        }
    }


    fun getData(): List<ReplayDTO> {
        return listOf(
            ReplayDTO(R.drawable.dummy_img, "제목1", "2024년 9월 11일", "좌타", "아이언", false),
            ReplayDTO(R.drawable.dummy_img, "제목2", "2024년 9월 12일", "우타", "드라이버", true),
            ReplayDTO(R.drawable.dummy_img, "제목3", "2024년 9월 13일", "우타", "아이언", false),
            ReplayDTO(R.drawable.dummy_img, "제목4", "2024년 9월 14일", "우타", "드라이버", true)
        )
    }


}