package com.ijonsabae.presentation.replay

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackCommentUseCase
import com.ijonsabae.domain.usecase.replay.HideSwingFeedbackUseCase
import com.ijonsabae.domain.usecase.replay.UpdateClampStatusUseCase
import com.ijonsabae.domain.usecase.replay.UpdateLikeStatusUseCase
import com.ijonsabae.domain.usecase.replay.UpdateTitleUseCase
import com.ijonsabae.presentation.BuildConfig
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentReplayBinding
import com.ijonsabae.presentation.main.MainActivity
import com.ijonsabae.presentation.mapper.SwingFeedbackCommentMapper
import com.ijonsabae.presentation.mapper.SwingFeedbackMapper
import com.ijonsabae.presentation.shot.SwingLocalDataProcessor
import com.ijonsabae.presentation.shot.SwingRemoteDataProcessor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import javax.inject.Inject

private const val TAG = "ReplayFragment_싸피"

const val S3_URL = BuildConfig.S3_URL
const val IMAGE = BuildConfig.IMAGE
const val THUMBNAIL = BuildConfig.THUMBNAIL
const val VIDEO = BuildConfig.VIDEO
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
    lateinit var hideSwingFeedbackUseCase: HideSwingFeedbackUseCase
    @Inject
    lateinit var getLocalSwingFeedbackCommentUseCase: GetLocalSwingFeedbackCommentUseCase
    @Inject
    lateinit var swingRemoteDataProcessor: SwingRemoteDataProcessor

    private val viewModel: ReplayFragmentViewModel by viewModels()

    private val replayAdapter by lazy {
        ReplayAdapter(fragmentContext).apply {
            setItemClickListener(
                object : ReplayAdapter.OnItemClickListener {
                    override fun onItemClick(item: SwingFeedback) {
                        val swingFeedbackCommentParcelable = runBlocking {
                            withContext(Dispatchers.IO) {
                                SwingFeedbackCommentMapper.mapperToSwingFeedbackParcelableList(
                                    getLocalSwingFeedbackCommentUseCase(item.userID,item.swingCode)
                                )
                            }
                        }
                        navController.navigate(ReplayFragmentDirections.actionReplayToReplayReport(
                            swingFeedbackCommentParcelable.toTypedArray(), SwingFeedbackMapper.mapperToSwingFeedbackSerializable(item)
                        ))
                    }

                    override fun onLikeClick(item: SwingFeedback) {
                        lifecycleScope.launch(coroutineExceptionHandler + Dispatchers.IO) {
                            updateLikeStatusUseCase(item.userID, item.swingCode, !item.likeStatus, System.currentTimeMillis())
                        }
                    }

                    override fun onItemDelete(item: SwingFeedback) {
                        lifecycleScope.launch(coroutineExceptionHandler + Dispatchers.IO) {
                            SwingLocalDataProcessor.deleteLocalSwingData(fragmentContext, item.swingCode, item.userID)
                            // 이건 Room에서 지우는 것
                            hideSwingFeedbackUseCase(item.userID, item.swingCode, System.currentTimeMillis())
                        }
                    }

                    override fun onTitleChange(item: SwingFeedback, title: String) {
                        lifecycleScope.launch(coroutineExceptionHandler + Dispatchers.IO) {
                            updateTitleUseCase(item.userID, item.swingCode, title, System.currentTimeMillis())
                        }
                    }

                    override fun changeClampStatus(item: SwingFeedback, clampStatus: Boolean) {
                                Log.d(TAG, "changeClampStatus: $clampStatus")
                        lifecycleScope.launch(Dispatchers.IO) {
                            updateClampStatusUseCase(item.userID, item.swingCode, clampStatus)
                        }
                    }

                    override fun onTouchListener(position: Int) {
                        Log.d(TAG, "onTouchListener: 터치 리스너 작동 $position")
                        //binding.rvReplay.scrollToPosition(position)
                    }
                }
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (fragmentContext as MainActivity).showAppBar("영상 다시보기")
        binding.cbFilter.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d(TAG, "onViewCreated: checkChangedListeener")
            // room에서 Flow를 받아서 갱신하는데, Flow의 내부 값을 갱신하는게 아니라 Flow 값 자체를 바꾸는 거니까 emit 같은거로 감지하는게 무용지물
            // 그냥 flow 자체를 바꾸면 직접 넣어줘야 함
            lifecycleScope.launch {
                if(isChecked){
                    viewModel.getLocalSwingFeedbackLikeList()
                }else{
                    viewModel.getLocalSwingFeedbackList()
                }
                initFlow()
            }

        }
        initFlow()
        initRecyclerView()
        initMoreBtn()
    }

    override fun onDestroyView() {
        val ivMore = requireActivity().findViewById<ImageView>(R.id.iv_more)
        ivMore.visibility = View.GONE
        super.onDestroyView()

    }

    private fun initFlow() {
        lifecycleScope.launch(coroutineExceptionHandler) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.swingFeedbackList.collectLatest{
                    Log.d(TAG, "initFlow: $it")
                    replayAdapter.submitData(it)
                }
            }
        }
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
            lifecycleScope.launch(coroutineExceptionHandler + Dispatchers.IO) {
                swingRemoteDataProcessor.uploadLocalSwingData(fragmentContext)
                withContext(Dispatchers.Main) {
                    showToastShort("내보내기가 완료됐습니다!")
                }
            }

            dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.menu_import).setOnClickListener {
            lifecycleScope.launch(coroutineExceptionHandler + Dispatchers.IO) {
               swingRemoteDataProcessor.downloadRemoteSwingData(fragmentContext)
                if(binding.cbFilter.isChecked){
                    lifecycleScope.launch {
                        viewModel.getLocalSwingFeedbackLikeList()
                        replayAdapter.submitData(viewModel.swingFeedbackList.value)
                    }
                }else{
                    lifecycleScope.launch {
                        viewModel.getLocalSwingFeedbackList()
                        replayAdapter.submitData(viewModel.swingFeedbackList.value)
                        Log.d(TAG, "onViewCreated: ${replayAdapter.itemCount}")
                    }
                }
                withContext(Dispatchers.Main){
                    showToastShort("가져오기가 완료됐습니다!")
                }
            }
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

//         여러 아이템이 한 번에 삭제 버튼이 보이는 경우 없도록 처리
        binding.rvReplay.apply {
            setOnTouchListener { v, event ->
                swipeHelper.removePreviousClamp(this)
                false
            }
        }
    }


}