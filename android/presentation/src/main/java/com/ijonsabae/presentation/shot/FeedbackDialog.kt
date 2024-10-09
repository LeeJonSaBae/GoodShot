package com.ijonsabae.presentation.shot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseDialog
import com.ijonsabae.presentation.databinding.DialogFeedbackBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

private const val TAG = "FeedbackDialog"

@AndroidEntryPoint
class FeedbackDialog :
    BaseDialog<DialogFeedbackBinding>(DialogFeedbackBinding::bind, R.layout.dialog_feedback) {
    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0
    private var tts: TextToSpeech? = null
    private var TTS_ID = "TTS"

    private val args: FeedbackDialogArgs by navArgs()
    private val checkListAdapter: CheckListAdapter by lazy {
        CheckListAdapter()
    }
    private val swingViewModel by activityViewModels<SwingViewModel>()

    private val skipMotionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == "SKIP_MOTION_DETECTED") {
                swingViewModel.setCurrentState(CameraState.ADDRESS)
                checkSwingCompletionAndNavigate()
                Log.d(
                    "processDetectedInfo",
                    "processDetectedInfo: SKIP_MOTION_DETECTED 인텐트 수신 in FeedbackDialog"
                )
                dismiss()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        registerLocalBroadCastReceiver()
        super.onViewCreated(view, savedInstanceState)
        initSoundPool()
        initTts()
        initBackPress()
        initButtons()
        initRecyclerView()
        setFeedback()
    }

    private fun executeTts() {
        Log.d(TAG, "executeTts: $tts")
        if (swingViewModel.currentState.value == CameraState.AGAIN) {
            tts?.speak("분석을 위해 다시 스윙해주세요!", TextToSpeech.QUEUE_FLUSH, null, TTS_ID)
        } else {
            val feedback = swingViewModel.getFeedBack()
            Log.d(TAG, "feedback: $feedback")
            feedback?.let {
                Log.d(TAG, "solution: ${it.feedBackSolution}")
                if (it.goodShot) soundPool.play(soundId, 0.8f, 0.8f, 1, 0, 1.0f)
                tts?.speak(it.feedBackSolution, TextToSpeech.QUEUE_FLUSH, null, TTS_ID)
            }
        }
    }

    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        // 오디오 파일 로드
        soundId = soundPool.load(fragmentContext, R.raw.applause, 1)
    }

    private fun initTts() {
        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.KOREAN)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "The Language is not supported!")
                } else {
                    Log.i(TAG, "TTS Initialization successful")
                    executeTts()
                }
            } else {
                Log.e(TAG, "TTS Initialization failed!")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setScreenWidthPercentage(0.9F)
        setScreenHeightPercentage(0.8F)
    }

    override fun onDestroyView() {
        swingViewModel.setCurrentState(CameraState.POSITIONING)
        super.onDestroyView()
    }

    override fun onDestroy() {
        tts?.let { t ->
            t.stop()
            t.shutdown()
        }
        soundPool.release()
        super.onDestroy()
    }

    private fun initButtons() {
        binding.btnClose.setOnClickListener {
            checkSwingCompletionAndNavigate()
            dismiss()
        }
    }

    private fun initBackPress() {
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                checkSwingCompletionAndNavigate()
                dismiss()
                true
            } else {
                false
            }
        }
    }

    private fun checkSwingCompletionAndNavigate() {
        if (args.swingCnt >= args.totalSwingCnt) {
            if (navController.currentDestination?.id == R.id.feedback_dialog) {
                navController.navigate(R.id.action_feedback_dialog_to_shot)
                showToastLong("스윙 촬영 횟수를 모두 채워 분석이 종료되었습니다.")
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvCheckList.adapter = checkListAdapter
        swingViewModel.getFeedBack()?.let {
            checkListAdapter.submitList(it.feedBackCheckList)
        }
    }

    private fun setFeedback() {
        swingViewModel.getFeedBack()?.apply {
            binding.apply {
                Glide.with(root)
                    .load(userSwingImage)
                    .into(ivMySwing)

                Glide.with(root)
                    .load(expertSwingImageResId)
                    .into(ivExpertSwing)

                tvTempo.text = tempo
                tvBack.text = back
                tvDown.text = down

                tvCheckListTitle.text = feedBackCheckListTitle
                tvFeedbackSolution.text = feedBackSolution
            }
        }

        binding.apply {
            "${args.swingCnt} / ${args.totalSwingCnt}".also { tvSwingCount.text = it }
        }
    }

    private fun registerLocalBroadCastReceiver() {
        LocalBroadcastManager.getInstance(fragmentContext).registerReceiver(
            skipMotionReceiver, IntentFilter("SKIP_MOTION_DETECTED")
        )
    }

    private fun PlayerView.setPlayer(uri: String) {
        val videoUri = Uri.parse(uri)
        val mediaItem = MediaItem.fromUri(videoUri)
        this.apply {
            useController = false
            player = ExoPlayer.Builder(fragmentContext).build().apply {
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true // 처음에 정지 상태
                repeatMode = Player.REPEAT_MODE_ALL
            }
        }

    }
}
