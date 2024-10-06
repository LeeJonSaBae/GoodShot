package com.ijonsabae.presentation.replay

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.usecase.replay.DeleteLocalSwingFeedbackUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackCommentUseCase
import com.ijonsabae.domain.usecase.replay.UpdateClampStatusUseCase
import com.ijonsabae.domain.usecase.replay.UpdateLikeStatusUseCase
import com.ijonsabae.domain.usecase.replay.UpdateTitleUseCase
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentReplayBinding
import com.ijonsabae.presentation.main.MainActivity
import com.ijonsabae.presentation.mapper.SwingFeedbackCommentMapper
import com.ijonsabae.presentation.mapper.SwingFeedbackMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "굿샷_ReplayFragment"

@AndroidEntryPoint
class ReplayFragment :
    BaseFragment<FragmentReplayBinding>(FragmentReplayBinding::bind, R.layout.fragment_replay) {
    @Inject
    lateinit var updateTitleUseCase: UpdateTitleUseCase

    @Inject
    lateinit var updateClampStatusUseCase: UpdateClampStatusUseCase

    @Inject
    lateinit var updateLikeStatusUseCase: UpdateLikeStatusUseCase

    @Inject
    lateinit var deleteLocalSwingFeedbackUseCase: DeleteLocalSwingFeedbackUseCase

    @Inject
    lateinit var getLocalSwingFeedbackCommentUseCase: GetLocalSwingFeedbackCommentUseCase

    private val viewModel: ReplayFragmentViewModel by viewModels()

    private val replayAdapter by lazy {
        ReplayAdapter(fragmentContext).apply {
            setItemClickListener(
                object : ReplayAdapter.OnItemClickListener {
                    override fun onItemClick(item: SwingFeedback) {
                        val swingFeedbackCommentParcelable = runBlocking {
                            withContext(Dispatchers.IO) {
                                SwingFeedbackCommentMapper.mapperToSwingFeedbackParcelableList(getLocalSwingFeedbackCommentUseCase(item.userID,item.swingCode))
                            }
                        }
                        navController.navigate(ReplayFragmentDirections.actionReplayToReplayReport(
                            swingFeedbackCommentParcelable.toTypedArray(), SwingFeedbackMapper.mapperToSwingFeedbackSerializable(item)
                        ))
                    }

                    override fun onLikeClick(item: SwingFeedback) {
                        lifecycleScope.launch(coroutineExceptionHandler + Dispatchers.IO) {
                            updateLikeStatusUseCase(item.userID, item.swingCode, !item.likeStatus)
                        }
                        Toast.makeText(context, "즐겨찾기 클릭~!", Toast.LENGTH_SHORT).show()
                    }

                    override fun onItemDelete(item: SwingFeedback) {
                        lifecycleScope.launch(coroutineExceptionHandler + Dispatchers.IO) {
                            TODO(
                                "문현" +
                                        "여기에 진짜 파일을 지우는 코드도 작성해야 함"
                            )
                            // 이건 Room에서 지우는 것
                            deleteLocalSwingFeedbackUseCase(item.userID, item.swingCode)
                        }
                    }

                    override fun onTitleChange(item: SwingFeedback, title: String) {
                        lifecycleScope.launch(coroutineExceptionHandler + Dispatchers.IO) {
                            updateTitleUseCase(item.userID, item.swingCode, title)
                        }
                    }

                    override fun changeClampStatus(item: SwingFeedback, clampStatus: Boolean) {
                        lifecycleScope.launch {
                            launch(Dispatchers.IO) {
                                updateClampStatusUseCase(item.userID, item.swingCode, clampStatus)
                            }
                        }
                    }
                }
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (fragmentContext as MainActivity).showAppBar("영상 다시보기")
        binding.cbFilter.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                Log.d(TAG, "onViewCreated: 체크 실행")
                viewModel.getLocalSwingFeedbackLikeList()
                lifecycleScope.launch {
                    viewModel.swingFeedbackList.collectLatest{
                        replayAdapter.submitData(it)
                    }
                }
            }else{
                viewModel.getLocalSwingFeedbackList()
                lifecycleScope.launch {
                    viewModel.swingFeedbackList.collectLatest{
                        replayAdapter.submitData(it)
                    }
                }
            }
        }
        initFlow()
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

    private fun initFlow() {
        lifecycleScope.launch(coroutineExceptionHandler) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.swingFeedbackList.collect{
                    Log.d(TAG, "initFlow: $it")
                    replayAdapter.submitData(it)
                }
            }
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
        binding.rvReplay.itemAnimator = null
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