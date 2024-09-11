package com.ijonsabae.presentation.shot

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.window.layout.WindowInfoTracker
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentCameraBinding
import com.ijonsabae.presentation.shot.flex.FoldingStateActor
import com.ijonsabae.presentation.util.PermissionChecker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CameraFragment 싸피"
@AndroidEntryPoint
class CameraFragment: BaseFragment<FragmentCameraBinding>(FragmentCameraBinding::bind, R.layout.fragment_camera) {


    lateinit var foldingStateActor: FoldingStateActor

    private lateinit var navController: NavController
    private lateinit var permissionChecker: PermissionChecker
    private lateinit var originalLayoutParams: LayoutParams
    private val permissionList = arrayOf(Manifest.permission.CAMERA)
    private var camera: Camera? = null

    private var cameraController: CameraControl? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(binding.root)
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

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            foldingStateActor.checkFoldingState(fragmentContext as AppCompatActivity, binding.camera, binding.layoutCamera)
        }
    }

    private fun startCamera() {

        // 1. CameraProvider 요청
        // ProcessCameraProvider는 Camera의 생명주기를 LifeCycleOwner의 생명주기에 Binding 함
        val cameraProviderFuture = ProcessCameraProvider.getInstance(fragmentContext)
        cameraProviderFuture.addListener({
            // 2. CameraProvier 사용 가능 여부 확인
            // 생명주기에 binding 할 수 있는 ProcessCameraProvider 객체 가져옴
            val cameraProvider = cameraProviderFuture.get()

            // 3. 카메라를 선택하고 use case를 같이 생명주기에 binding

            // 3-1. Preview를 생성 → Preview를 통해서 카메라 미리보기 화면을 구현.
            // surfaceProvider는 데이터를 받을 준비가 되었다는 신호를 카메라에게 보내준다.
            // setSurfaceProvider는 PreviewView에 SurfaceProvider를 제공해준다.
            val preview = Preview.Builder().build()
            preview.surfaceProvider = binding.camera.surfaceProvider
            // 아래처럼 써도 됨
//           val preview = Preview.Builder().build().also {
//               it.setSurfaceProvider(mBinding.viewFinder.surfaceProvider)
//           }

            // 3-2. 카메라 세팅을 한다. (useCase는 bindToLifecycle에서)
            // CameraSelector는 카메라 세팅을 맡는다.(전면, 후면 카메라)
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // binding 전에 binding 초기화
                cameraProvider.unbindAll()

                // 3-3. use case와 카메라를 생명 주기에 binding
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview)

                cameraController = camera!!.cameraControl
                cameraController!!.setZoomRatio(1F) // 1x Zoom
            } catch (exc: Exception) {
                println("에러 $exc")
                Log.d(TAG, "startCamera: 에러 $exc")
            }

            // 4. Preview를 PreviewView에 연결한다.
            // surfaceProvider는 데이터를 받을 준비가 되었다는 신호를 카메라에게 보내준다.
            // setSurfaceProvider는 PreviewView에 SurfaceProvider를 제공해준다.
            preview.surfaceProvider = binding.camera.surfaceProvider
            originalLayoutParams = binding.camera.layoutParams
        }, ContextCompat.getMainExecutor(fragmentContext))
    }
}