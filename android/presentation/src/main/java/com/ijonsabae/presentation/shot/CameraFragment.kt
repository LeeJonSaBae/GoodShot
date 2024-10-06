package com.ijonsabae.presentation.shot

import android.Manifest
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.SpannableString
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.text.style.UpdateAppearance
import android.util.Log
import android.util.Size
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.common.util.concurrent.ListenableFuture
import com.ijonsabae.domain.usecase.login.GetUserIdUseCase
import com.ijonsabae.domain.usecase.replay.GetSwingFeedBackUseCase
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentCameraBinding
import com.ijonsabae.presentation.main.MainActivity
import com.ijonsabae.presentation.shot.CameraState.ADDRESS
import com.ijonsabae.presentation.shot.CameraState.AGAIN
import com.ijonsabae.presentation.shot.CameraState.ANALYZING
import com.ijonsabae.presentation.shot.CameraState.POSITIONING
import com.ijonsabae.presentation.shot.CameraState.RESULT
import com.ijonsabae.presentation.shot.CameraState.SWING
import com.ijonsabae.presentation.shot.ai.camera.CameraSource
import com.ijonsabae.presentation.shot.flex.FoldingStateActor
import com.ijonsabae.presentation.util.PermissionChecker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import kotlin.math.roundToInt

private const val TAG = "CameraFragment_싸피"

@AndroidEntryPoint
class CameraFragment :
    BaseFragment<FragmentCameraBinding>(FragmentCameraBinding::bind, R.layout.fragment_camera) {


    @Inject
    lateinit var foldingStateActor: FoldingStateActor

    @Inject
    lateinit var getUserIdUseCase: GetUserIdUseCase

    //TODO: Inject 유즈케이스 받아서 lateinitvar로 받아서 camerasource에 던져주면 된다
    private val permissionList = arrayOf(Manifest.permission.CAMERA)
    private var camera: Camera? = null
    private var cameraController: CameraControl? = null
    private val lastAnalysisTimestamp = AtomicLong(0L)
    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0
    private var tts: TextToSpeech? = null
    private var TTS_ID = "TTS"

    private val swingViewModel by activityViewModels<SwingViewModel>()
    private val shotSettingViewModel by activityViewModels<ShotSettingViewModel>()

    /** A [SurfaceView] for camera preview.   */
    private lateinit var surfaceView: SurfaceView

    private lateinit var cameraSource: CameraSource

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider

    @Inject
    lateinit var getSwingFeedBackUseCase: GetSwingFeedBackUseCase
    private var isSelf = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 자동 화면 꺼짐 방지
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        navController = Navigation.findNavController(binding.root)
        (fragmentContext as MainActivity).hideAppBar()
        initObservers()
        initTts()
        initSoundPool()
        surfaceView = binding.camera
        permissionChecker = PermissionChecker(this)
        permissionChecker.setOnGrantedListener { //퍼미션 획득 성공일때
            startCamera()
        }
        if (permissionChecker.checkPermission(fragmentContext, permissionList)) {
            Log.d(TAG, "onViewCreated: 통과")
            permissionChecker.permitted.onGranted()
        } else {
            Log.d(TAG, "onViewCreated: 권한 부족")
            permissionChecker.requestPermissionLauncher.launch(permissionList) // 권한없으면 창 띄움
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
                }
            } else {
                Log.e(TAG, "TTS Initialization failed!")
            }
        }
    }

    private fun stopTts() {
        if (tts?.isSpeaking == true) {
            tts?.stop()
        }
    }

    private fun startCamera() {
        // 1. CameraProvider 요청
        // ProcessCameraProvider는 Camera의 생명주기를 LifeCycleOwner의 생명주기에 Binding 함
        cameraProviderFuture = ProcessCameraProvider.getInstance(fragmentContext)
        cameraProviderFuture.addListener({
            // 2. CameraProvier 사용 가능 여부 확인
            // 생명주기에 binding 할 수 있는 ProcessCameraProvider 객체 가져옴
            cameraProvider = cameraProviderFuture.get()
            // CameraSelector는 카메라 세팅을 맡는다.(전면, 후면 카메라)
            cameraSetting()
        }, ContextCompat.getMainExecutor(fragmentContext))
    }

    private fun cameraSetting() {
        try {
            cameraProvider.unbindAll()
            val cameraSelector = if (isSelf) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(
                    ResolutionSelector.Builder()
                        .setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
                        .setResolutionStrategy(
                            ResolutionStrategy(
                                Size(360, 480),
                                ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                            )
                        )
                        .build()
                )
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                        // 성능 분석 로깅
                        val currentTimestamp = System.currentTimeMillis()
                        val lastTimestamp = lastAnalysisTimestamp.getAndSet(currentTimestamp)
                        if (lastTimestamp != 0L) {
                            val deltaTime = currentTimestamp - lastTimestamp
                            val fps = 1000.0 / deltaTime
                            Log.d("CameraAnalyzer", "Current FPS: ${fps.roundToInt()}")
                        }
                        cameraSource.processImage(
                            cameraSource.getRotateBitmap(
                                image.toBitmap(),
                                isSelf
                            ), isSelf
                        )
                        image.close()
                    }
                }

            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, imageAnalyzer
            )
            cameraController = camera!!.cameraControl
            cameraController!!.setZoomRatio(1F) // 1x Zoom
        } catch (exc: Exception) {
            Log.d(TAG, "Camera Setting Error: $exc")
        }
    }

    override fun onResume() {
        super.onResume()
        (fragmentContext as MainActivity).hideBottomNavBar()
        initClickListener()
        initAiSetting()
        cameraSource.resume()

        lifecycleScope.launch {
            foldingStateActor.checkFoldingStateForCamera(
                fragmentContext as AppCompatActivity,
                swingViewModel.currentState.value,
                binding.progressTitle,
                binding.tvResultHeader,
                binding.tvResultSubHeader,
                binding.camera,
                binding.ivAlert,
                binding.tvAlert,
                binding.layoutCameraMenu,
                binding.layoutAlert,
                binding.layoutCamera,
                binding.indicatorProgress
            )
        }
    }

    override fun onPause() {
        stopTts()
        cameraSource.pause()
        super.onPause()
    }

    override fun onDestroyView() {
        (fragmentContext as MainActivity).showBottomNavBar()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        cameraProvider.unbindAll()
        super.onDestroyView()
    }

    override fun onDestroy() {
        tts?.let { t ->
            t.stop()
            t.shutdown()
        }
        cameraSource.destroy()
        soundPool.release()
        super.onDestroy()
    }

    private fun initObservers() {
        swingViewModel.currentState.observe(viewLifecycleOwner) { state ->
            var text: String
            var color: Int
            when (state) {
                POSITIONING -> {
                    binding.tvAlert.visibility = View.VISIBLE
                    binding.ivAlert.visibility = View.VISIBLE

                    binding.ivBar.visibility = View.GONE
                    binding.tvCircleTempo.visibility = View.GONE
                    binding.tvTitleTempo.visibility = View.GONE
                    binding.tvCircleBackswing.visibility = View.GONE
                    binding.tvTitleBackswing.visibility = View.GONE
                    binding.tvCircleDownswing.visibility = View.GONE
                    binding.tvTitleDownswing.visibility = View.GONE
                    binding.tvResultHeader.visibility = View.GONE
                    binding.tvResultSubHeader.visibility = View.GONE

                    binding.tvAnalyzing.visibility = View.GONE
                    binding.progressTitle.visibility = View.GONE
                    binding.indicatorProgress.visibility = View.GONE

                    binding.ivAlert.setImageBitmap(
                        ContextCompat.getDrawable(
                            fragmentContext,
                            R.drawable.yellow_card
                        )!!.toBitmap()
                    )

                    text = "전신이 모두 보이도록 조금 더 뒤로 가주세요!!"
                    color = ContextCompat.getColor(fragmentContext, R.color.yello_card)
                }

                ADDRESS -> {
                    binding.tvAlert.visibility = View.VISIBLE
                    binding.ivAlert.visibility = View.VISIBLE

                    binding.ivBar.visibility = View.GONE
                    binding.tvCircleTempo.visibility = View.GONE
                    binding.tvTitleTempo.visibility = View.GONE
                    binding.tvCircleBackswing.visibility = View.GONE
                    binding.tvTitleBackswing.visibility = View.GONE
                    binding.tvCircleDownswing.visibility = View.GONE
                    binding.tvTitleDownswing.visibility = View.GONE
                    binding.tvResultHeader.visibility = View.GONE
                    binding.tvResultSubHeader.visibility = View.GONE

                    binding.tvAnalyzing.visibility = View.GONE
                    binding.progressTitle.visibility = View.GONE
                    binding.indicatorProgress.visibility = View.GONE

                    binding.ivAlert.setImageBitmap(
                        ContextCompat.getDrawable(
                            fragmentContext,
                            R.drawable.address_icon
                        )!!.toBitmap()
                    )

                    text = "어드레스 자세를 잡아주세요!"
                    color = ContextCompat.getColor(fragmentContext, R.color.address_color)
                }

                SWING -> {
                    binding.tvAlert.visibility = View.VISIBLE
                    binding.ivAlert.visibility = View.VISIBLE

                    binding.ivBar.visibility = View.GONE
                    binding.ivBar.visibility = View.GONE
                    binding.tvCircleTempo.visibility = View.GONE
                    binding.tvTitleTempo.visibility = View.GONE
                    binding.tvCircleBackswing.visibility = View.GONE
                    binding.tvTitleBackswing.visibility = View.GONE
                    binding.tvCircleDownswing.visibility = View.GONE
                    binding.tvTitleDownswing.visibility = View.GONE
                    binding.tvResultHeader.visibility = View.GONE
                    binding.tvResultSubHeader.visibility = View.GONE

                    binding.tvAnalyzing.visibility = View.GONE
                    binding.progressTitle.visibility = View.GONE
                    binding.indicatorProgress.visibility = View.GONE

                    binding.ivAlert.setImageBitmap(
                        ContextCompat.getDrawable(
                            fragmentContext,
                            R.drawable.swing_icon
                        )!!.toBitmap()
                    )

                    text = "스윙해주세요!"
                    color = ContextCompat.getColor(fragmentContext, R.color.swing_color)
                }

                ANALYZING -> {
                    binding.tvAlert.visibility = View.VISIBLE
                    binding.ivAlert.visibility = View.GONE

                    binding.ivBar.visibility = View.GONE
                    binding.tvCircleTempo.visibility = View.GONE
                    binding.tvTitleTempo.visibility = View.GONE
                    binding.tvCircleBackswing.visibility = View.GONE
                    binding.tvTitleBackswing.visibility = View.GONE
                    binding.tvCircleDownswing.visibility = View.GONE
                    binding.tvTitleDownswing.visibility = View.GONE
                    binding.tvResultHeader.visibility = View.GONE
                    binding.tvResultSubHeader.visibility = View.GONE

                    binding.tvAnalyzing.visibility = View.GONE
                    binding.progressTitle.visibility = View.VISIBLE
                    binding.indicatorProgress.apply {
                        setProgressCompat(90, true)
                        isIndeterminate = true
                        show()
                        visibility = View.VISIBLE
                    }
                    text = "스윙 영상 분석중..."
                    color = ContextCompat.getColor(fragmentContext, R.color.black)
                    // 텍스트에 적용할 그라디언트 색상 설정
                    val spannableText = SpannableString(binding.progressTitle.text)

                    // 그라디언트 적용을 위한 Custom Span 클래스 생성
                    class GradientSpan(
                        private val startColor: Int,
                        private val midColor: Int,
                        private val endColor: Int
                    ) : CharacterStyle(),
                        UpdateAppearance {
                        override fun updateDrawState(textPaint: android.text.TextPaint) {
                            val width = textPaint.measureText(spannableText.toString())
                            textPaint.shader = LinearGradient(
                                0f, 0f, width, binding.progressTitle.textSize,
                                intArrayOf(startColor, midColor, endColor),
                                floatArrayOf(0.30F, 0.80F, 1F), Shader.TileMode.CLAMP
                            )
                        }
                    }

                    // SpannableString에 그라디언트 Span 적용 (전체 텍스트에 적용)
                    spannableText.apply {
                        setSpan(
                            GradientSpan(
                                ContextCompat.getColor(
                                    fragmentContext,
                                    R.color.swing_inference_gradient_gray
                                ),
                                ContextCompat.getColor(
                                    fragmentContext,
                                    R.color.swing_inference_gradient_mid_green
                                ),
                                ContextCompat.getColor(
                                    fragmentContext,
                                    R.color.swing_inference_gradient_end_green
                                )
                            ),
                            0, spannableText.length, 0
                        )
                        setSpan(StyleSpan(Typeface.ITALIC), 0, this.length, 0)
                        binding.progressTitle.text = this
                    }
                }

                AGAIN -> {
                    binding.tvAlert.visibility = View.VISIBLE
                    binding.ivAlert.visibility = View.VISIBLE

                    binding.ivBar.visibility = View.GONE
                    binding.ivBar.visibility = View.GONE
                    binding.tvCircleTempo.visibility = View.GONE
                    binding.tvTitleTempo.visibility = View.GONE
                    binding.tvCircleBackswing.visibility = View.GONE
                    binding.tvTitleBackswing.visibility = View.GONE
                    binding.tvCircleDownswing.visibility = View.GONE
                    binding.tvTitleDownswing.visibility = View.GONE
                    binding.tvResultHeader.visibility = View.GONE
                    binding.tvResultSubHeader.visibility = View.GONE

                    binding.indicatorProgress.hide()
                    binding.tvAnalyzing.visibility = View.GONE
                    binding.progressTitle.visibility = View.GONE
                    binding.indicatorProgress.visibility = View.GONE

                    binding.ivAlert.setImageBitmap(
                        ContextCompat.getDrawable(
                            fragmentContext,
                            R.drawable.again_icon
                        )!!.toBitmap()
                    )
                    text = "분석을 위해 다시 스윙해주세요!"
                    binding.tvAlert.text = text
                    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, TTS_ID)
                    color = ContextCompat.getColor(fragmentContext, R.color.like_yellow)
                }

                RESULT -> {
                    binding.tvAlert.visibility = View.GONE
                    binding.ivAlert.visibility = View.GONE

                    binding.ivBar.visibility = View.GONE
                    binding.tvCircleTempo.visibility = View.GONE
                    binding.tvTitleTempo.visibility = View.GONE
                    binding.tvCircleBackswing.visibility = View.GONE
                    binding.tvTitleBackswing.visibility = View.GONE
                    binding.tvCircleDownswing.visibility = View.GONE
                    binding.tvTitleDownswing.visibility = View.GONE
                    binding.tvResultHeader.visibility = View.GONE
                    binding.tvResultSubHeader.visibility = View.GONE

                    binding.indicatorProgress.hide()
                    binding.tvAnalyzing.visibility = View.GONE
                    binding.progressTitle.visibility = View.GONE
                    binding.indicatorProgress.visibility = View.GONE

                    navController.navigate(
                        CameraFragmentDirections.actionCameraToFeedbackDialog(
                            swingViewModel.getSwingCnt(),
                            shotSettingViewModel.totalSwingCnt.value
                        )
                    )
                    val feedback = swingViewModel.getFeedBack()
                    feedback?.let {
                        if (it.goodShot) soundPool.play(soundId, 0.8f, 0.8f, 1, 0, 1.0f)
                        tts?.speak(it.feedBackSolution, TextToSpeech.QUEUE_FLUSH, null, TTS_ID)
                    }

                    text = "스윙 분석 결과"
                    color = ContextCompat.getColor(fragmentContext, R.color.black)
                }
            }
            binding.tvAlert.text = text
            binding.layoutAlert.setBackgroundColor(color)
        }
    }

    private fun initClickListener() {
        binding.btnCameraChange.setOnClickListener {
            isSelf = !isSelf
            cameraSetting()
        }

        binding.btnClose.setOnClickListener {
            navController.navigate(R.id.action_camera_to_shot)
        }
    }

    //TODO 영민 : 1. userID 넘겨주기, 2. swingfeedbackviewmodel에 SwingFeedback객체 저장하는 함수 넘겨주기
    private fun initAiSetting() {
        if (!::cameraSource.isInitialized) {
            cameraSource = CameraSource(
                fragmentContext,
                shotSettingViewModel.isLeft.value,
                { swingViewModel.currentState.value },
                { cameraState -> swingViewModel.setCurrentState(cameraState) },
                { feedback -> swingViewModel.setFeedBack(feedback) },
                { swingViewModel.getUserId() },
                { swingFeedback -> swingViewModel.insertSwingFeedback(swingFeedback) },
                { swingFeedbackComment -> swingViewModel.insertSwingFeedbackComment(swingFeedbackComment) },
                swingViewModel::initializeSwingCnt,
                swingViewModel::increaseSwingCnt,
            )
            cameraSource.setSurfaceView(binding.camera)
        }
    }

}