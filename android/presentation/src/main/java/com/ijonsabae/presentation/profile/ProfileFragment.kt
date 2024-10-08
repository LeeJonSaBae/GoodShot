package com.ijonsabae.presentation.profile

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.config.Const.Companion.GalleryPermission
import com.ijonsabae.presentation.databinding.FragmentProfileBinding
import com.ijonsabae.presentation.login.LoginActivity
import com.ijonsabae.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

private const val TAG = "ProfileFragment 싸피"

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::bind, R.layout.fragment_profile) {
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri: Uri? = data?.data

                if (imageUri != null) {
                    startCrop(imageUri)
                } else {
                    showToastShort("이미지를 선택할 수 없습니다.")
                }
            }
        }
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { croppedUri ->
                lifecycleScope.launch {
                    profileViewModel.setCroppedUri(croppedUri)
                    setUserProfileImage(croppedUri)
                }
            }
        } else {
            val error = result.error
            showToastShort("Crop Failed: ${error?.message}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getUserInfo()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (fragmentContext as MainActivity).showAppBar("마이 페이지")
        init()
        initFlow()
        permissionChecker.setOnGrantedListener{
            openGallery()
        }
    }

    private fun init() {
        binding.btnEditProfileImg.setOnClickListener {
            checkPermissionAndOpenGallery()
        }
        binding.layoutChangePassword.setOnClickListener {
            navController.navigate(R.id.action_profile_to_change_password_dialog)
        }

        binding.layoutGoTotalReport.setOnClickListener {
            showTotalReport()
        }

        binding.layoutLogout.setOnClickListener {
            lifecycleScope.launch(coroutineExceptionHandler) {
                profileViewModel.logout()
            }
        }

        binding.layoutResign.setOnClickListener {
            navController.navigate(R.id.action_profile_to_resign_dialog)
        }

        binding.layoutGoLogin.setOnClickListener {
            navController.navigate(R.id.action_profile_to_login_dialog)
        }
    }

    private fun initFlow(){
        lifecycleScope.launch(coroutineExceptionHandler) {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    profileViewModel.isLogoutSucceed.collect { result ->
                        if (result == 200) {
                            showToastShort("로그아웃 되었습니다!")
//
//                            val intent = Intent(fragmentContext, LoginActivity::class.java).apply {
//                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                            }
//                            startActivity(intent)
//                            requireActivity().finish()
                        }
                    }
                }
                launch {
                    profileViewModel.croppedUri.drop(1).collect{ croppedUri ->
                        launch(coroutineExceptionHandler) {
                            // Presigned URL 받아오기
                            val imageExtension =
                                getImageExtension(fragmentContext.contentResolver, croppedUri)
                            Log.d(TAG, "확장자: $imageExtension")
                            if (imageExtension != null) {
                                profileViewModel.getPresignedURL(imageExtension)
                            } else {
                                showToastShort("확장자가 없습니다!")
                            }
                        }
                    }
                }
                launch {
                    // 프로필 이미지 upload
                    profileViewModel.presignedUrl.drop(1).collect { presignedUrl ->
                        Log.d(TAG, "presignedUrl: $presignedUrl")
                        presignedUrl?.let {
                            profileViewModel.uploadProfileImage(presignedUrl, profileViewModel.croppedUri.value).getOrThrow()
                        }
                    }
                }
                launch {
                    profileViewModel.imageUrl.drop(1).collect { imageUrl ->
                        val result = profileViewModel.updateProfile(imageUrl).getOrThrow()
                        if(result.code == 200){
                            showToastShort("프로필 수정이 완료되었습니다!")
                        }else{
                            showToastShort("프로필 수정에 실패했습니다!")
                        }

                    }
                }
                launch {
                    profileViewModel.isLogin.collect {
                        Log.d(TAG, "initFlow: ${it}")
                        
                        getUserInfo()
                    }
                }
            }
        }
    }

    private fun startCrop(uri: Uri) {
        cropImage.launch(
            CropImageContractOptions(
                uri = uri,
                cropImageOptions = CropImageOptions(
                    outputCompressFormat = Bitmap.CompressFormat.PNG
                )
            )
        )
    }

    private fun getUserInfo() {
        lifecycleScope.launch(coroutineExceptionHandler) {
            Log.d(TAG, "getUserInfo: ${profileViewModel.getToken()}")
            if(profileViewModel.getToken() == null){
                setGuestUI()
            }else{
                setUserUI()
                profileViewModel.getProfileInfo()
            }

            profileViewModel.profileInfo.collect { profileInfo ->
                Log.d(TAG, "profileInfo: $profileInfo")
                if (profileInfo != null) {
                    setUserInfo(
                        profileImgUrl = Uri.parse(profileInfo.profileUrl),
                        name = profileInfo.name
                    )
                }
            }
        }
    }

    private fun setGuestUI(){
        binding.apply {
            layoutLogin.visibility = View.INVISIBLE
            layoutGoLogin.visibility = View.VISIBLE
        }
    }

    private fun setUserUI(){
        binding.apply {
            layoutLogin.visibility = View.VISIBLE
            layoutGoLogin.visibility = View.INVISIBLE
        }
    }

    private fun setUserInfo(profileImgUrl: Uri, name: String) {
        setUserProfileImage(profileImgUrl)
        setUserProfileName(name)
    }

    private fun setUserProfileImage(profileImgUrl: Uri) {
        Glide.with(this)
            .load(profileImgUrl)
            .into(binding.ivProfileImg)
    }

    private fun setUserProfileName(name: String) {
        binding.tvName.text = name
    }

    private fun showTotalReport() {
        findNavController().navigate(R.id.action_profile_to_totalReport)
    }

    private fun checkPermissionAndOpenGallery() {
        if(permissionChecker.checkPermission(fragmentContext, GalleryPermission)){
            openGallery()
        }else{
            showToastShort("권한을 설정하셔야 기록 서비스를 이용 가능합니다!")
            //ask for permission
            permissionChecker.requestPermissionLauncher.launch(GalleryPermission)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    private fun getImageExtension(contentResolver: ContentResolver, uri: Uri): String? {
        val mimeType = contentResolver.getType(uri)

        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }
}