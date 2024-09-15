package com.ijonsabae.presentation.shot

import android.Manifest
import android.graphics.Bitmap
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.text.style.UpdateAppearance

import android.util.Log
import android.util.Size
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
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
import com.ijonsabae.presentation.shot.CameraState.*
import com.ijonsabae.presentation.shot.ai.camera.CameraSource
import com.ijonsabae.presentation.shot.ai.data.Person
import com.ijonsabae.presentation.shot.ai.utils.VisualizationUtils
import com.ijonsabae.presentation.shot.flex.FoldingStateActor
import com.ijonsabae.presentation.util.PermissionChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

private const val TAG = "CameraFragment_싸피"

class CameraFragment :
    BaseFragment<FragmentCameraBinding>(FragmentCameraBinding::bind, R.layout.fragment_camera) {
    private lateinit var navController: NavController
    private lateinit var foldingStateActor: FoldingStateActor
    private lateinit var permissionChecker: PermissionChecker
    private lateinit var originalLayoutParams: LayoutParams
    private val permissionList = arrayOf(Manifest.permission.CAMERA)
    private var camera: Camera? = null
    private var cameraController: CameraControl? = null

    private val swingViewModel by activityViewModels<SwingViewModel>()

    private var cameraSource: CameraSource? = null

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    private var isSelf = true

    companion object {
        private const val MIN_CONFIDENCE = .2f
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(binding.root)

        // 카메라 상태를 변경해주기 위해 옵저버 등록
        initObservers()

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
            cameraProvider = cameraProviderFuture.get()

            initAiSetting()
            cameraSetting()
        }, ContextCompat.getMainExecutor(fragmentContext))
    }

    private fun cameraSetting() {
        try {
            cameraProvider.unbindAll()

            val preview = Preview.Builder().build()

            val cameraSelector = if (isSelf) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            val imageAnalyzer = ImageAnalysis
                .Builder()
                .setTargetResolution(
                    Size(
                        binding.previewView.width,
                        binding.previewView.height
                    )
                )// 원하는 해상도 설정
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                        val rotatedBitmap = getRotateBitmap(
                            image.toBitmap(),
                            binding.previewView.width,
                            binding.previewView.height,
                            isSelf
                        )

                        lifecycleScope.launch(Dispatchers.Default) {
                            val person = cameraSource?.processImage(rotatedBitmap)
                            person?.let {
                                if (it.score > MIN_CONFIDENCE) {
                                    val outputBitmap = VisualizationUtils.drawBodyKeypoints(
                                        Bitmap.createBitmap(
                                            binding.layoutCanvas.width,
                                            binding.layoutCanvas.height,
                                            Bitmap.Config.ARGB_8888
                                        ),
                                        listOf(it)
                                    )
                                    withContext(Dispatchers.Main) {
                                        updateCanvasWithBitmap(outputBitmap)
                                    }
                                } else {
                                    requireActivity().runOnUiThread {
                                        // binding.layoutCanvas에 관절 아무것도 그리지 말기
                                        binding.layoutCanvas.removeAllViews()
                                        binding.layoutCanvas.invalidate()
                                    }
                                }
                            }
                        }
                        image.close()
                    }

                }

            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, imageAnalyzer
            )
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview
            )

            cameraController = camera!!.cameraControl
            cameraController!!.setZoomRatio(1F) // 1x Zoom

            preview.surfaceProvider = binding.previewView.surfaceProvider
            originalLayoutParams = binding.previewView.layoutParams
        } catch (exc: Exception) {
            println("에러 $exc")
            Log.d(TAG, "startCamera: 에러 $exc")
        }
    }

    private fun updateCanvasWithBitmap(bitmap: Bitmap) {
        val imageView =
            binding.layoutCanvas.getChildAt(0) as? ImageView ?: ImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.FIT_CENTER
                binding.layoutCanvas.addView(this)
            }
        imageView.setImageBitmap(bitmap)
    }


    private fun getRotateBitmap(bitmap: Bitmap, width: Int, height: Int, self: Boolean): Bitmap {
        val rotateMatrix = Matrix()

        if (self) {
            rotateMatrix.postRotate(270.0f)
            // 전면 카메라 화면은 좌우 반전 되서 들어와서 좌우 반전 필요, but estimatePoses 수행시 한번 더 뒤집어야 하기 때문에 반전 불필요
            // rotateMatrix.postScale(-1F, 1F)
        } else {
            rotateMatrix.postRotate(90.0f)
        }

        val rotateBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, rotateMatrix, false)

        // Bitmap과 View의 비율 계산
        val bitmapRatio = rotateBitmap.width.toFloat() / rotateBitmap.height.toFloat()
        val viewRatio = width.toFloat() / height.toFloat()

        var croppedBitmap = rotateBitmap

        // Bitmap이 View보다 가로로 길 때 (비율에 맞게 가로를 자름)
        if (bitmapRatio > viewRatio) {
            val newWidth = (rotateBitmap.height * viewRatio).toInt()
            val cropStartX = (rotateBitmap.width - newWidth) / 2
            // 가로를 자르고 중앙에 맞추기
            croppedBitmap =
                Bitmap.createBitmap(rotateBitmap, cropStartX, 0, newWidth, rotateBitmap.height)
        }
        // Bitmap이 View보다 세로로 길 때 (비율에 맞게 세로를 자름)
        else if (bitmapRatio < viewRatio) {
            val newHeight = (rotateBitmap.width / viewRatio).toInt()
            val cropStartY = (rotateBitmap.height - newHeight) / 2
            // 세로를 자르고 중앙에 맞추기
            croppedBitmap =
                Bitmap.createBitmap(rotateBitmap, 0, cropStartY, rotateBitmap.width, newHeight)
        }

        // 크기를 View의 크기에 맞게 확장
        return Bitmap.createScaledBitmap(croppedBitmap, width, height, false)
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

                    binding.indicatorProgress.apply {
                        setProgressCompat(90, true)
                        isIndeterminate = true
                        show()
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

    override fun onResume() {
        super.onResume()
        (fragmentContext as MainActivity).hideBottomNavBar()
        initClickListener()
        initAiSetting()

        lifecycleScope.launch {
            foldingStateActor.checkFoldingState(
                fragmentContext as AppCompatActivity,
                swingViewModel.currentState.value,
                binding.progressTitle,
                binding.tvResultHeader,
                binding.tvResultSubHeader,
                binding.previewView,
                binding.ivAlert,
                binding.tvAlert,
                binding.layoutCameraMenu,
                binding.layoutAlert,
                binding.layoutCamera
            )
        }
    }

    override fun onDestroyView() {
        (fragmentContext as MainActivity).showBottomNavBar()
        cameraProvider.unbindAll()
        super.onDestroyView()
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

    private fun initAiSetting() {
        if (cameraSource == null) {
            cameraSource = CameraSource(fragmentContext, swingViewModel)
        }
    }

    override fun onDestroy() {
        cameraSource?.destroy()
        super.onDestroy()
    }

}