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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ijonsabae.domain.model.SwingCommentExportParam
import com.ijonsabae.domain.model.SwingComparisonParam
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.model.SwingFeedbackExportParam
import com.ijonsabae.domain.usecase.profile.UploadPresignedDataUseCase
import com.ijonsabae.domain.usecase.replay.DeleteLocalSwingFeedbackUseCase
import com.ijonsabae.domain.usecase.replay.ExportSwingFeedbackListUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalChangedSwingFeedbackListUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackCommentUseCase
import com.ijonsabae.domain.usecase.replay.HideSwingFeedbackUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackDataNeedSyncUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackListNeedToUploadUseCase
import com.ijonsabae.domain.usecase.replay.GetRemoteSwingFeedbackListNeedToUploadUseCase
import com.ijonsabae.domain.usecase.replay.SyncUpdateStatusUseCase
import com.ijonsabae.domain.usecase.replay.UpdateClampStatusUseCase
import com.ijonsabae.domain.usecase.replay.UpdateLikeStatusUseCase
import com.ijonsabae.domain.usecase.replay.UpdateTitleUseCase
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.config.Const.Companion.BACKSWING
import com.ijonsabae.presentation.config.Const.Companion.DOWNSWING
import com.ijonsabae.presentation.config.Const.Companion.NICE
import com.ijonsabae.presentation.databinding.FragmentReplayBinding
import com.ijonsabae.presentation.main.MainActivity
import com.ijonsabae.presentation.mapper.SwingFeedbackCommentMapper
import com.ijonsabae.presentation.mapper.SwingFeedbackMapper
import com.ijonsabae.presentation.mapper.SwingFeedbackSyncRoomDataMapper
import com.ijonsabae.presentation.shot.SwingLocalDataProcessor
import com.ijonsabae.presentation.util.formatTDateFromLongKorea
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "ReplayFragment_싸피"

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
    lateinit var hideSwingFeedbackUseCase: HideSwingFeedbackUseCase

    @Inject
    lateinit var getLocalSwingFeedbackCommentUseCase: GetLocalSwingFeedbackCommentUseCase

    @Inject
    lateinit var getChangedSwingFeedbackUseCase: GetLocalChangedSwingFeedbackListUseCase
    
    @Inject
    lateinit var getSwingFeedbackDataNeedSyncUseCase: GetLocalSwingFeedbackDataNeedSyncUseCase

    @Inject
    lateinit var getLocalSwingFeedbackListNeedToUploadUseCase: GetLocalSwingFeedbackListNeedToUploadUseCase

    @Inject
    lateinit var syncUpdateStatusUseCase: SyncUpdateStatusUseCase

    @Inject
    lateinit var getRemoteSwingFeedbackListNeedToUploadUseCase: GetRemoteSwingFeedbackListNeedToUploadUseCase

    @Inject
    lateinit var uploadPresignedDataUseCase: UploadPresignedDataUseCase

    @Inject
    lateinit var exportSwingFeedbackListUseCase: ExportSwingFeedbackListUseCase

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
                        showToastShort("즐겨찾기 클릭~!")
                    }

                    override fun onItemDelete(item: SwingFeedback) {
                        lifecycleScope.launch(coroutineExceptionHandler + Dispatchers.IO) {
                            SwingLocalDataProcessor.deleteLocalSwingData(fragmentContext, item.swingCode, item.userID)
                            // 이건 Room에서 지우는 것
                            hideSwingFeedbackUseCase(item.userID, item.swingCode, System.currentTimeMillis())
//                            deleteLocalSwingFeedbackUseCase(item.userID, item.swingCode)
                        }
                    }

                    override fun onTitleChange(item: SwingFeedback, title: String) {
                        lifecycleScope.launch(coroutineExceptionHandler + Dispatchers.IO) {
                            updateTitleUseCase(item.userID, item.swingCode, title, System.currentTimeMillis())
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
        if(binding.cbFilter.isChecked){
            viewModel.getLocalSwingFeedbackLikeList()
            lifecycleScope.launch {
                replayAdapter.submitData(viewModel.swingFeedbackList.value)
            }
        }else{
            viewModel.getLocalSwingFeedbackList()
            lifecycleScope.launch {
                replayAdapter.submitData(viewModel.swingFeedbackList.value)
                Log.d(TAG, "onViewCreated: ${replayAdapter.itemCount}")
            }
        }
        (fragmentContext as MainActivity).showAppBar("영상 다시보기")
        binding.cbFilter.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                viewModel.getLocalSwingFeedbackLikeList()
            }else{
                viewModel.getLocalSwingFeedbackList()
            }
            // room에서 Flow를 받아서 갱신하는데, Flow의 내부 값을 갱신하는게 아니라 Flow 값 자체를 바꾸는 거니까 emit 같은거로 감지하는게 무용지물
            // 그냥 flow 자체를 바꾸면 직접 넣어줘야 함
            lifecycleScope.launch {
                replayAdapter.submitData(viewModel.swingFeedbackList.value)
            }
        }
        initRecyclerView()
        initMoreBtn()
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
            lifecycleScope.launch(coroutineExceptionHandler + Dispatchers.IO) {
                val userID = viewModel.getUserID()
                Log.d(TAG, "showCustomPopup: $userID")
                if(userID == -1L){
                    launch(Dispatchers.Main) {
                        showToastShort("비회원은 동기화를 진행하실 수 없습니다!")
                    }
                }else{
                    // 바꿔야 할 SwingFeedback 목록 추출
                    val changedSwingFeedbackList = SwingFeedbackSyncRoomDataMapper.mapperToSwingFeedbackSyncList(getChangedSwingFeedbackUseCase(userID))
                    // 서버에 업데이트 보냄
                    getSwingFeedbackDataNeedSyncUseCase(changedSwingFeedbackList).getOrThrow()

                    // 이후 업데이트 보냈으니까 삭제된 것 제외 전부 업데이트 0으로 해서 업데이트 필요 없는 값으로 설정
                    syncUpdateStatusUseCase(userID)

                    // 0인 목록들을 뽑아서 Upload 후보군을 생성
                    val list = getLocalSwingFeedbackListNeedToUploadUseCase(userID)

                    // 업로드 후보군으로부터 진짜 업로드 대상을 추출
                    val result = getRemoteSwingFeedbackListNeedToUploadUseCase(SwingComparisonParam(list.map { it.swingCode})).getOrThrow()

                    // 진짜 업로드 대상에 대해서 반복적으로 각 대상마다 10개의 영상, 이미지를 업로드
                    result.data.forEach { data ->
                        val poseList = SwingLocalDataProcessor.getSwingPoseFiles(fragmentContext, data.code, userID)
                        data.presignedUrls.forEachIndexed { index, url ->
                            when (index) {
                                0 -> {
                                    uploadPresignedDataUseCase(url, SwingLocalDataProcessor.getSwingVideoFile(fragmentContext,data.code, userID).toURI()).getOrThrow()
                                }
                                1 -> {
                                    uploadPresignedDataUseCase(url, SwingLocalDataProcessor.getSwingThumbnailFile(fragmentContext,data.code, userID).toURI()).getOrThrow()
                                }
                                else -> {
                                    uploadPresignedDataUseCase(url, poseList[index-2].toURI()).getOrThrow()
                                }
                            }
                        }
                    }

                    // 업로드 대상에 대해서 돌아가면서 해당 업로드한 코드의 comment와 feedback을 가져와서 SwingExportParam 형성 및 Upload
                    val codeSet = result.data.map { it.code }.toHashSet()

                    exportSwingFeedbackListUseCase(
                        list.filter {
                            it.swingCode in codeSet
                        }.map { it ->
                            val data =
                                SwingFeedbackExportParam(
                                    score = it.score,
                                    title = it.title,
                                    similarity = it.similarity,
                                    likeStatus = it.likeStatus,
                                    tempo = it.tempo,
                                    solution = it.solution,
                                    id = userID,
                                    code = it.swingCode,
                                    time = formatTDateFromLongKorea(it.date),
                                    backSwingComments = getLocalSwingFeedbackCommentUseCase(userID,it.swingCode).filter { comment ->
                                        comment.poseType == BACKSWING
                                    }.map { SwingCommentExportParam(
                                        commentType = if(it.commentType == NICE){"NICE"} else{"BAD"},
                                        content = it.content
                                    ) },
                                    downSwingComments = getLocalSwingFeedbackCommentUseCase(userID,it.swingCode).filter {
                                        it.poseType == DOWNSWING
                                    }.map { SwingCommentExportParam(
                                        commentType = if(it.commentType == NICE){"NICE"} else{"BAD"},
                                        content = it.content
                                    ) }
                                )
                            Log.d(TAG, "showCustomPopup 업로드 데이터: $data")
                            data
                        }
                    ).getOrThrow()
                }
                withContext(Dispatchers.Main) {
                    showToastShort("내보내기가 완료됐습니다!")
                }
            }

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
        mountainRecyclerView.adapter = replayAdapter.apply {
            lifecycleScope.launch {
                submitData(viewModel.swingFeedbackList.last())
            }
        }

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