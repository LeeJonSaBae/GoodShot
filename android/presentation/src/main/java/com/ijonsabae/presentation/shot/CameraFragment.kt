package com.ijonsabae.presentation.shot

import ModelType
import MoveNet
import android.Manifest
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.text.style.UpdateAppearance
import android.util.Log
import android.util.Size
import android.view.SurfaceView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.window.layout.WindowInfoTracker
import com.google.common.util.concurrent.ListenableFuture
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentCameraBinding
import com.ijonsabae.presentation.main.MainActivity
import com.ijonsabae.presentation.shot.CameraState.ADDRESS
import com.ijonsabae.presentation.shot.CameraState.ANALYZING
import com.ijonsabae.presentation.shot.CameraState.POSITIONING
import com.ijonsabae.presentation.shot.CameraState.RESULT
import com.ijonsabae.presentation.shot.CameraState.SWING
import com.ijonsabae.presentation.shot.ai.camera.CameraSource
import com.ijonsabae.presentation.shot.ai.data.Device
import com.ijonsabae.presentation.shot.flex.FoldingStateActor
import com.ijonsabae.presentation.util.PermissionChecker
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

private const val TAG = "CameraFragment_싸피"

class CameraFragment :
    BaseFragment<FragmentCameraBinding>(FragmentCameraBinding::bind, R.layout.fragment_camera) {
    private lateinit var navController: NavController
    private lateinit var foldingStateActor: FoldingStateActor
    private lateinit var permissionChecker: PermissionChecker
    private lateinit var originalLayoutParams: ConstraintLayout.LayoutParams
    private val permissionList = arrayOf(Manifest.permission.CAMERA)
    private var camera: Camera? = null
    private var cameraController: CameraControl? = null

    private val swingViewModel by activityViewModels<SwingViewModel>()

    /** A [SurfaceView] for camera preview.   */
    private lateinit var surfaceView: SurfaceView

    /** Default device is CPU */
    private var device = Device.CPU
    private var cameraSource: CameraSource? = null
    private var poseDetector: MoveNet? = null  // MoveNet 인스턴스를 저장할 변수 추가


    /** 카메라 처리 간격을 조절하기 위한 변수 */
    private val fpsInterval = 1000 / 24 // 24 FPS에 해당하는 프레임 간격 (밀리초)
    private var lastAnalyzedTimestamp = 0L

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    private var isSelf = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(binding.root)
        /******* AI 카메라 코드 시작 *******/
        // 카메라 상태를 변경해주기 위해 옵저버 등록
//        initObservers()
        surfaceView = binding.camera
        /******* AI 카메라 코드 끝 *******/
        foldingStateActor = FoldingStateActor(WindowInfoTracker.getOrCreate(fragmentContext))
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

    private fun startCamera() {
        // 1. CameraProvider 요청
        // ProcessCameraProvider는 Camera의 생명주기를 LifeCycleOwner의 생명주기에 Binding 함
        cameraProviderFuture = ProcessCameraProvider.getInstance(fragmentContext)
        cameraProviderFuture.addListener({
            // 2. CameraProvier 사용 가능 여부 확인
            // 생명주기에 binding 할 수 있는 ProcessCameraProvider 객체 가져옴
            initAiSetting()
            cameraProvider = cameraProviderFuture.get()
            /** AI 세팅 */
            if (cameraSource == null) {
                cameraSource = CameraSource(requireContext(), swingViewModel, surfaceView)
            }
            val cameraProvider = cameraProviderFuture.get()
            // 3-2. 카메라 세팅을 한다. (useCase는 bindToLifecycle에서)
            // CameraSelector는 카메라 세팅을 맡는다.(전면, 후면 카메라)
            val cameraSelector = if(isSelf){
                CameraSelector.DEFAULT_FRONT_CAMERA
            }else{
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            try {
                // binding 전에 binding 초기화
                cameraProvider.unbindAll()
                // 관절 트래킹이 더해진 View를 위한 ImageAnalysis
                // 3-3. use case와 카메라를 생명 주기에 binding
                val imageAnalyzer = ImageAnalysis
                    .Builder()
                    .setTargetResolution(
                        Size(
                            binding.camera.width,
                            binding.camera.height
                        )
                    )// 원하는 해상도 설정
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                            cameraSource?.let {
                                it.processImage(
                                    it.rotateBitmap(image.toBitmap(),surfaceView.width, surfaceView.height, isSelf)  // 이미지 처리 함수 호출
                                )  // 이미지 처리 함수 호출
                            }
                            val currentTimestamp = System.currentTimeMillis()
                            if (currentTimestamp - lastAnalyzedTimestamp >= fpsInterval) {
                                lastAnalyzedTimestamp = currentTimestamp
                                cameraSource?.processImage(
                                    cameraSource!!.rotateBitmap(
                                        image.toBitmap(),
                                        surfaceView.width,
                                        surfaceView.height,
                                        true
                                    )
                                )
                            }
                            image.close()
                        }
                    }

                // 3-3. use case와 카메라를 생명 주기에 binding
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, imageAnalyzer
                )
                cameraController = camera!!.cameraControl
                cameraController!!.setZoomRatio(1F) // 1x Zoom
            } catch (exc: Exception) {
                println("에러 $exc")
                Log.d(TAG, "startCamera: 에러 $exc")
            }

            // 4. Preview를 PreviewView에 연결한다.
            // surfaceProvider는 데이터를 받을 준비가 되었다는 신호를 카메라에게 보내준다.
            // setSurfaceProvider는 PreviewView에 SurfaceProvider를 제공해준다.
//            preview.surfaceProvider = binding.camera.surfaceProvider
            originalLayoutParams = binding.camera.layoutParams as ConstraintLayout.LayoutParams
        }, ContextCompat.getMainExecutor(fragmentContext))
    }

    override fun onResume() {
        super.onResume()
        (fragmentContext as MainActivity).hideBottomNavBar()
        initClickListener()
        initAiSetting()
        createPoseEstimator()
        cameraSource?.resume()

        lifecycleScope.launch {
            foldingStateActor.checkFoldingState(
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
                binding.layoutCamera
            )
        }
    }

    override fun onPause() {
        cameraSource?.pause()
        super.onPause()
    }

    override fun onDestroyView() {
        (fragmentContext as MainActivity).showBottomNavBar()
        cameraProvider.unbindAll()
        super.onDestroyView()
    }

    override fun onDestroy() {
        cameraSource?.destroy()
        super.onDestroy()
    }

    private fun initObservers() {
        swingViewModel.currentState.observe(viewLifecycleOwner) { state ->
            var text: String
            var color: Int
            when (state) {
                POSITIONING ->{
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
                    class GradientSpan(private val startColor: Int, private val midColor: Int, private val endColor: Int) : CharacterStyle(),
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
                            GradientSpan(ContextCompat.getColor(fragmentContext, R.color.swing_inference_gradient_gray), ContextCompat.getColor(fragmentContext, R.color.swing_inference_gradient_mid_green), ContextCompat.getColor(fragmentContext, R.color.swing_inference_gradient_end_green)),
                            0, spannableText.length, 0
                        )
                        setSpan(StyleSpan(Typeface.ITALIC), 0, this.length, 0)
                        binding.progressTitle.text = this
                    }

                    binding.indicatorProgress.apply {
                        setProgressCompat(90, true)
                        isIndeterminate = true
                        show()
                    }
                }
                RESULT -> {
                    binding.tvAlert.visibility = View.GONE
                    binding.ivAlert.visibility = View.GONE

                    binding.ivBar.visibility = View.VISIBLE
                    binding.tvCircleTempo.visibility = View.VISIBLE
                    binding.tvTitleTempo.visibility = View.VISIBLE
                    binding.tvCircleBackswing.visibility = View.VISIBLE
                    binding.tvTitleBackswing.visibility = View.VISIBLE
                    binding.tvCircleDownswing.visibility = View.VISIBLE
                    binding.tvTitleDownswing.visibility = View.VISIBLE
                    binding.tvResultHeader.visibility = View.VISIBLE
                    binding.tvResultSubHeader.visibility = View.VISIBLE

                    binding.indicatorProgress.hide()
                    binding.tvAnalyzing.visibility = View.GONE
                    binding.progressTitle.visibility = View.GONE
                    binding.indicatorProgress.visibility = View.GONE

                    text = "스윙 분석 결과"
                    color = ContextCompat.getColor(fragmentContext, R.color.black)
                }
            }
            binding.tvAlert.text = text
            binding.layoutAlert.setBackgroundColor(color)
        }
    }

    private fun initClickListener(){
        binding.btnCameraChange.setOnClickListener{
            cameraProvider.unbindAll()

            try {
                // 새로운 카메라를 바인딩
                isSelf = !isSelf
                val cameraSelector = if(isSelf){
                    CameraSelector.DEFAULT_FRONT_CAMERA
                }else{
                    CameraSelector.DEFAULT_BACK_CAMERA
                }

                val imageAnalyzer = ImageAnalysis
                    .Builder()
                    .setTargetResolution(
                        Size(
                            binding.camera.width,
                            binding.camera.height
                        )
                    )// 원하는 해상도 설정
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                            cameraSource?.let {
                                it.processImage(
                                    it.rotateBitmap(image.toBitmap(),surfaceView.width, surfaceView.height, isSelf)  // 이미지 처리 함수 호출
                                )  // 이미지 처리 함수 호출
                            }

                            image.close()
                        }
                    }

                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e("CameraSwitch", "Failed to bind camera use cases", exc)
            }
        }
        binding.btnClose.setOnClickListener{
            navController.navigate(R.id.action_camera_to_shot)
        }
    }

    private fun initAiSetting(){
        if (cameraSource == null) {
            cameraSource = CameraSource(fragmentContext, swingViewModel, surfaceView)
        }
        createPoseEstimator()
    }

    private fun createPoseEstimator() {
        // For MoveNet MultiPose, hide score and disable pose classifier as the model returns
        // multiple Person instances.
        val poseDetector = MoveNet.create(requireContext(), device, ModelType.Lightning)

        poseDetector.let { detector ->
            this.poseDetector = detector as? MoveNet  // MoveNet 인스턴스 저장
            cameraSource?.setDetector(detector)
        }
    }
}