package com.ijonsabae.presentation.profile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ijonsabae.domain.usecase.login.GetUserIdUseCase
import com.ijonsabae.domain.usecase.profile.GetTotalReportUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackListUseCase
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseDialog
import com.ijonsabae.presentation.databinding.DialogProgressBinding
import com.ijonsabae.presentation.mapper.TotalReportMapper
import com.ijonsabae.presentation.shot.SwingRemoteDataProcessor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "ProgressDialog_싸피"
@AndroidEntryPoint
class ProgressDialog:
    BaseDialog<DialogProgressBinding>(DialogProgressBinding::bind, R.layout.dialog_progress) {
        @Inject
        lateinit var swingRemoteDataProcessor: SwingRemoteDataProcessor
        @Inject
        lateinit var getUserIdUseCase: GetUserIdUseCase
        @Inject
        lateinit var getLocalSwingFeedbackListUseCase: GetLocalSwingFeedbackListUseCase
        @Inject
        lateinit var getTotalReportUseCase: GetTotalReportUseCase

    private val progressReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val progress =intent.getIntExtra("progress" , 0)
            if(binding.indicatorDeterminateProgress.progress + progress <= 100){
                binding.indicatorDeterminateProgress.progress += progress
            }else{
                binding.indicatorDeterminateProgress.progress = 100
            }
            binding.tvProgress.text = binding.indicatorDeterminateProgress.progress.toString() + "%"

            if(binding.indicatorDeterminateProgress.progress == 100){
                lifecycleScope.launch(coroutineExceptionHandler+ Dispatchers.IO) {
                    val userId = getUserIdUseCase()
                    val result = getLocalSwingFeedbackListUseCase(userId)
                    if(result.size < 16){
                        launch(Dispatchers.Main){
                            navController.navigate(R.id.action_progress_dialog_to_forbidden_dialog)
                        }
                    }else{
                        val totalReport = TotalReportMapper.mapperTotalReportParcelable(getTotalReportUseCase().getOrThrow().data)
                        withContext(Dispatchers.Main){
                            navController.navigate(ProgressDialogDirections.actionProgressDialogToProfileTotalReport(totalReport))
                        }
                    }
                    withContext(Dispatchers.Main){
                        dismiss()
                    }
                }
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
        showToastShort("종합 결과를 분석 중입니다!")
        binding.indicatorIndeterminateProgress.apply {
            setProgressCompat(90, true)
            isIndeterminate = true
            show()
            visibility = View.VISIBLE
        }
        binding.indicatorDeterminateProgress.apply {
            show()
            visibility = View.GONE
        }
        registerLocalBroadCastReceiver()
        setScreenWidthPercentage(0.9F)
        setScreenHeightConstraint(WRAP_CONTENT)
        lifecycleScope.launch(coroutineExceptionHandler + Dispatchers.IO) {
            swingRemoteDataProcessor.downloadRemoteSwingData(fragmentContext)
            swingRemoteDataProcessor.uploadLocalSwingData(fragmentContext)
        }
    }

    override fun onDestroyView() {
        LocalBroadcastManager.getInstance(fragmentContext).unregisterReceiver(
            progressReceiver
        )
        super.onDestroyView()
    }

    private fun registerLocalBroadCastReceiver() {
        LocalBroadcastManager.getInstance(fragmentContext).registerReceiver(
            progressReceiver, IntentFilter("progress_sync")
        )
    }
}