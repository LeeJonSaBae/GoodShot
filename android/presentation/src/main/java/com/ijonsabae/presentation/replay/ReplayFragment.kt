package com.ijonsabae.presentation.replay

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ijonsabae.domain.model.Replay
import com.ijonsabae.domain.usecase.replay.GetReplayUseCase
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentReplayBinding
import com.ijonsabae.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "굿샷_ReplayFragment"

@AndroidEntryPoint
class ReplayFragment :
    BaseFragment<FragmentReplayBinding>(FragmentReplayBinding::bind, R.layout.fragment_replay) {

    @Inject
    lateinit var getReplayUseCase: GetReplayUseCase

    private val replayAdapter by lazy {
        ReplayAdapter(requireContext()).apply {
            setItemClickListener(
                object : ReplayAdapter.OnItemClickListener {
                    override fun onItemClick(item: Replay) {
                        findNavController().navigate(R.id.action_replay_to_replayReport)
                    }

                    override fun onLikeClick(item: Replay, check: Boolean) {
                        Toast.makeText(context, "즐겨찾기 클릭~!", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (fragmentContext as MainActivity).showAppBar("영상 다시보기")

        initMoreBtn()

        initRecyclerView()
    }

    override fun onDestroyView() {
        val ivMore = requireActivity().findViewById<ImageView>(R.id.iv_more)
        ivMore.visibility = View.GONE
        super.onDestroyView()

    }

    private fun initMoreBtn() {
        val ivMore = requireActivity().findViewById<ImageView>(R.id.iv_more)
        ivMore.visibility = View.VISIBLE

        ivMore.setOnClickListener {
            showCustomPopup(ivMore)

        }
    }

    private fun showCustomPopup(anchorView: View) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_popup_menu, null)
        val dialog = Dialog(requireContext())

        dialog.setContentView(dialogView)
        dialog.window?.setWindowAnimations(R.style.DialogAnimation)

        dialogView.findViewById<View>(R.id.menu_export).setOnClickListener {
            Toast.makeText(requireContext(), "내보내기 클릭", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.menu_import).setOnClickListener {
            Toast.makeText(requireContext(), "가져오기 클릭", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        setDialogLocation(dialog, anchorView)
        dialog.show()
    }

    private fun setDialogLocation(dialog: Dialog, anchorView: View) {
        val window = dialog.window
        window?.setDimAmount(0f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        val anchorX = location[0]
        val anchorY = location[1]
        Log.d(TAG, "setDialogLocation: $anchorX, $anchorY")

        window?.setGravity(Gravity.TOP or Gravity.START)
        val params: WindowManager.LayoutParams? = window?.attributes
        params?.x = anchorX - 350
        params?.y = anchorY
        window?.attributes = params
    }

    private fun initRecyclerView() {
        val list = getReplayUseCase().getOrThrow()
        replayAdapter.submitList(list)
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


}