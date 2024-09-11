package com.ijonsabae.presentation.shot

import MoveNet
import PoseMatcher
import VideoEncoder
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Process
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.window.layout.WindowInfoTracker
import com.google.gson.GsonBuilder
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentCameraBinding
import com.ijonsabae.presentation.shot.CameraState.*
import com.ijonsabae.presentation.shot.ai.camera.CameraSource
import com.ijonsabae.presentation.shot.ai.data.BodyPart.*
import com.ijonsabae.presentation.shot.ai.data.Device
import com.ijonsabae.presentation.shot.ai.data.Person
import com.ijonsabae.presentation.shot.ai.vo.FrameData
import com.ijonsabae.presentation.shot.ai.vo.VideoData
import com.ijonsabae.presentation.shot.flex.FoldingStateActor
import com.ijonsabae.presentation.util.PermissionChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

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

    private val cameraViewModel by activityViewModels<CameraViewModel>()

    /** A [SurfaceView] for camera preview.   */
    private lateinit var surfaceView: SurfaceView

    /** Default device is CPU */
    private var device = Device.CPU
    private lateinit var poseMatcher: PoseMatcher
    private var cameraSource: CameraSource? = null
    private var poseDetector: MoveNet? = null  // MoveNet 인스턴스를 저장할 변수 추가
    private var isClassifyPose = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(binding.root)
        /******* AI 카메라 코드 시작 *******/
        //자세 분류기
        poseMatcher = PoseMatcher(requireContext())
        // 카메라 상태를 변경해주기 위해 옵저버 등록
        initObservers()
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
        val cameraProviderFuture = ProcessCameraProvider.getInstance(fragmentContext)
        cameraProviderFuture.addListener({
            // 2. CameraProvier 사용 가능 여부 확인
            // 생명주기에 binding 할 수 있는 ProcessCameraProvider 객체 가져옴


            if (cameraSource == null) {
                cameraSource =
                    CameraSource(surfaceView, cameraListener)
                    isPoseClassifier()
//                lifecycleScope.launch(Dispatchers.Main) {
//                    cameraSource?.initCamera()
//                }
            }
            createPoseEstimator()
            val cameraProvider = cameraProviderFuture.get()

            // 3-2. 카메라 세팅을 한다. (useCase는 bindToLifecycle에서)
            // CameraSelector는 카메라 세팅을 맡는다.(전면, 후면 카메라)
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // binding 전에 binding 초기화
                cameraProvider.unbindAll()

                // 관절 트래킹이 더해진 View를 위한 ImageAnalysis
                // 3-3. use case와 카메라를 생명 주기에 binding
                val imageAnalyzer = ImageAnalysis
                    .Builder()
                    .setTargetResolution(Size(binding.camera.width, binding.camera.height))// 원하는 해상도 설정
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                            cameraSource!!.processImage(cameraSource!!.rotateBitmap(image.toBitmap(), true))  // 이미지 처리 함수 호출
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

    private fun initObservers() {
        cameraViewModel.currentState.observe(viewLifecycleOwner) { state ->
            val text: String = when (state) {
                POSITIONING -> "전신이 모두 보이도록 조금 더 뒤로 가주세요!!"
                ADDRESS -> "어드레스 자세를 잡아주세요!"
                SWING -> "스윙해주세요!"
                ANALYZING -> "스윙 영상 분석중..."
                RESULT -> "스윙 분석 결과"
            }
            binding.tvTest.text = text
        }
    }

    override fun onResume() {
        super.onResume()
        cameraSource?.resume()
        lifecycleScope.launch {
            foldingStateActor.checkFoldingState(
                fragmentContext as AppCompatActivity,
                binding.camera,
                binding.layoutCamera
            )
        }
    }

    override fun onPause() {
        cameraSource?.close()
        cameraSource = null
        super.onPause()
    }

    private fun isPoseClassifier() {
        cameraSource?.setClassifier(if (isClassifyPose) PoseClassifier.create(requireContext()) else null)
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


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun captureLastThreeSeconds() {
        val executionTime = measureTimeMillis {
            poseDetector?.let { moveNet ->
                val (imagesWithTimestamps, jointData) = moveNet.getQueuedData()
                if (imagesWithTimestamps.isNotEmpty()) {
                    Log.d("MainActivity_Capture", "캡처할 이미지 수: ${imagesWithTimestamps.size}")

                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                        Date()
                    )
                    val videoFileName = "pose_$timestamp.mp4"
                    val videoFile = File(requireContext().cacheDir, videoFileName)

                    val videoEncoder = VideoEncoder(
                        imagesWithTimestamps[0].first.width,
                        imagesWithTimestamps[0].first.height,
                        24,
                        videoFile.absolutePath
                    )
                    videoEncoder.start()

                    val frameData = mutableListOf<FrameData>()

                    imagesWithTimestamps.forEachIndexed { index, (bitmap, imageTimestamp) ->
                        val byteBuffer = bitmapToByteBuffer(bitmap)
                        videoEncoder.encodeFrame(byteBuffer)

                        // 각 프레임의 관절 데이터 저장
                        val keyPoints = jointData.getOrNull(index)
                        if (keyPoints != null) {
                            frameData.add(
                                FrameData(imageTimestamp, keyPoints)
                            )
                        }
                    }

                    videoEncoder.finish()

                    // 자세 분류 수행
                    val poseClassificationTime = measureTimeMillis {
                        val lastJointData = jointData.lastOrNull()
                        if (lastJointData != null) {
                            val (poseName, probability) = poseMatcher.classifyPose(lastJointData)
                            Log.d(
                                "PoseClassification",
                                "마지막 프레임 자세: $poseName, 확률: ${probability * 100}%"
                            )
                        } else {
                            Log.d("PoseClassification", "자세 분류를 위한 관절 데이터가 없습니다.")
                        }
                    }
                    Log.d(
                        "PoseClassification_Performance",
                        "자세 분류 수행 시간: $poseClassificationTime ms"
                    )

                    val uri = saveVideoToGallery(videoFile)
                    uri?.let {
                        showToast("비디오가 갤러리에 저장되었습니다.")
                        Log.d("MainActivity_Capture", "비디오 URI: $it")
                        MediaScannerConnection.scanFile(
                            requireContext(),
                            arrayOf(it.toString()),
                            null,
                            null
                        )

                        // JSON 파일로 관절 데이터 저장
                        saveKeyPointsToJson(videoFileName, frameData)

                    } ?: showToast("비디오 저장에 실패했습니다.")

                    // 임시 파일 삭제
                    videoFile.delete()
                } else {
                    Log.d("MainActivity_Capture", "캡처할 이미지가 없습니다")
                    showToast("캡처할 이미지가 없습니다.")
                }
            } ?: run {
                Log.d("MainActivity_Capture", "MoveNet 인스턴스가 초기화되지 않았습니다")
                showToast("MoveNet 인스턴스가 초기화되지 않았습니다.")
            }
        }
        Log.d("MainActivity_Performance", "captureLastThreeSeconds 함수 실행 시간: $executionTime ms")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveKeyPointsToJson(videoFileName: String, frameData: List<FrameData>) {
        val videoData = VideoData(videoFileName, frameData)
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(videoData)

        val jsonFileName = "${videoFileName.removeSuffix(".mp4")}.json"

        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, jsonFileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOCUMENTS + "/PoseEstimation"
                )
            }

            val contentResolver = requireContext().contentResolver

            val uri = contentResolver.insert(
                MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                contentValues
            )
            uri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(jsonString.toByteArray())
                }
                Log.d("MainActivity_Save", "JSON 파일이 저장되었습니다: $uri")
                showToast("관절 데이터가 JSON 파일로 저장되었습니다.")
            } ?: run {
                Log.e("MainActivity_Save", "JSON 파일을 저장할 수 없습니다.")
                showToast("관절 데이터 저장에 실패했습니다.")
            }
        } catch (e: Exception) {
            Log.e("MainActivity_Save", "JSON 파일 저장 중 오류 발생", e)
            showToast("관절 데이터 저장 중 오류가 발생했습니다.")
        }
    }

    private fun bitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val inputWidth = bitmap.width
        val inputHeight = bitmap.height
        val yuvImage = ByteArray(inputWidth * inputHeight * 3 / 2)
        val argb = IntArray(inputWidth * inputHeight)

        bitmap.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight)

        encodeYUV420SP(yuvImage, argb, inputWidth, inputHeight)

        return ByteBuffer.wrap(yuvImage)
    }

    private fun encodeYUV420SP(yuv420sp: ByteArray, argb: IntArray, width: Int, height: Int) {
        val frameSize = width * height
        var yIndex = 0
        var uvIndex = frameSize

        for (j in 0 until height) {
            for (i in 0 until width) {
                val rgb = argb[j * width + i]
                val r = rgb shr 16 and 0xFF
                val g = rgb shr 8 and 0xFF
                val b = rgb and 0xFF
                val y = (66 * r + 129 * g + 25 * b + 128 shr 8) + 16
                yuv420sp[yIndex++] = y.toByte()

                if (j % 2 == 0 && i % 2 == 0) {
                    val u = (-38 * r - 74 * g + 112 * b + 128 shr 8) + 128
                    val v = (112 * r - 94 * g - 18 * b + 128 shr 8) + 128
                    yuv420sp[uvIndex++] = u.toByte()
                    yuv420sp[uvIndex++] = v.toByte()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveVideoToGallery(videoFile: File): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, videoFile.name)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(
                MediaStore.Video.Media.RELATIVE_PATH,
                Environment.DIRECTORY_MOVIES + "/PoseEstimation"
            ) // 수정된 부분
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        val resolver = requireContext().contentResolver
        val collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(collection, values)

        uri?.let {
            resolver.openOutputStream(it)?.use { os ->
                videoFile.inputStream().use { it.copyTo(os) }
            }
            values.clear()
            values.put(MediaStore.Video.Media.IS_PENDING, 0)
            resolver.update(it, values, null, null)
        }

        return uri
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }


    // check if permission is granted or not.
    private fun isCameraPermissionGranted(): Boolean {
        return requireContext().checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    private var lastLogTime = 0L

    private fun logWithThrottle(message: String) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastLogTime >= 1000) { // 1초 이상 지났는지 확인
            Log.d("싸피", message)
            lastLogTime = currentTime
        }
    }


    // 이전 실행 시간으로부터 최소 1초 지나야 실행
    private var lastExecutionTime = 0L
    private val cameraListener = object : CameraSource.CameraSourceListener {
        override fun onDetectedInfo(person: Person) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastExecutionTime >= 1000) {
                lastExecutionTime = currentTime
                processDetectedInfo(person)
            }
        }
    }

    private fun processDetectedInfo(person: Person) {
        val point = person.keyPoints

//        logWithThrottle(
//            "name: ${point[LEFT_WRIST.position].bodyPart}, x: ${point[LEFT_WRIST.position].coordinate.x}, y: ${point[LEFT_ANKLE.position].coordinate.y}\n" +
//                    "name: ${point[RIGHT_WRIST.position].bodyPart}, x: ${point[RIGHT_WRIST.position].coordinate.x}, y: ${point[RIGHT_ANKLE.position].coordinate.y}"
//        )

        // 1. 몸 전체가 카메라 화면에 들어오는지 체크
        if ((point[NOSE.position].score) < 0.3 ||
            (point[LEFT_ANKLE.position].score) < 0.3 ||
            (point[RIGHT_ANKLE.position].score < 0.3)
        ) {
            cameraViewModel.setCurrentState(POSITIONING)
        }
        // 2. 어드레스 자세 체크
        else if ((point[LEFT_WRIST.position].coordinate.y > point[LEFT_ELBOW.position].coordinate.y &&
                    point[RIGHT_WRIST.position].coordinate.y > point[RIGHT_ELBOW.position].coordinate.y &&
                    point[LEFT_WRIST.position].coordinate.x >= point[LEFT_SHOULDER.position].coordinate.x &&
                    point[LEFT_WRIST.position].coordinate.x <= point[RIGHT_SHOULDER.position].coordinate.x &&
                    point[RIGHT_WRIST.position].coordinate.x <= point[RIGHT_SHOULDER.position].coordinate.x &&
                    point[RIGHT_WRIST.position].coordinate.x >= point[LEFT_SHOULDER.position].coordinate.x).not()
        ) {
            cameraViewModel.setCurrentState(ADDRESS)
        } else {
            cameraViewModel.setCurrentState(SWING)
        }
    }
}