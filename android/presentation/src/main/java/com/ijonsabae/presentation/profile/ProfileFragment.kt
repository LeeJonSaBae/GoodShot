package com.ijonsabae.presentation.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentProfileBinding
import com.ijonsabae.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "굿샷_ProfileFragment"

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::bind, R.layout.fragment_profile) {

    private val profileViewModel: ProfileViewModel by viewModels()
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri: Uri? = data?.data

                if (imageUri != null) {
                    startCrop(imageUri)
                } else {
                    Toast.makeText(requireContext(), "이미지를 선택할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val croppedUri = result.uriContent
            if (croppedUri != null) {
                setUserProfileImage(croppedUri)
                lifecycleScope.launch(coroutineExceptionHandler) {
                    // Presigned URL 받아오기
                    val imageExtension =
                        getImageExtension(requireContext().contentResolver, croppedUri)
//                    Log.d(TAG, "확장자: $imageExtension")
                    if (imageExtension != null) {
                        profileViewModel.getPresignedURL(imageExtension)
                    } else {
                        Toast.makeText(requireContext(), "확장자가 없습니다!", Toast.LENGTH_SHORT).show()
                    }

                    // 프로필 이미지 upload
                    profileViewModel.presignedUrl.collect { presignedUrl ->
                        Log.d(TAG, "presignedUrl: $presignedUrl")
                        presignedUrl?.let {
                            profileViewModel.uploadProfileImage(
                                presignedUrl, croppedUri
                            )
                        }
                    }
                }
            }


        } else {
            val error = result.error
            Toast.makeText(requireContext(), "Crop failed: ${error?.message}", Toast.LENGTH_SHORT)
                .show()
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getUserInfo()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun getUserInfo() {
        lifecycleScope.launch(coroutineExceptionHandler) {
            profileViewModel.getProfileInfo()

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

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (fragmentContext as MainActivity).showAppBar("마이 페이지")
        init()
    }

    private fun init() {

        binding.btnEditProfileImg.setOnClickListener {
            checkPermissionAndOpenGallery()
        }
        binding.layoutChangePassword.setOnClickListener {
            showCustomDialog()
        }

        binding.layoutGoTotalReport.setOnClickListener {
            showTotalReport()
        }

        binding.layoutResign.setOnClickListener {
            showResignDialog()
        }
    }

    private fun showCustomDialog() {
        val customDialog = ChangePasswordDialog()
        customDialog.show(parentFragmentManager, "CustomDialogFragment")
    }

    private fun showTotalReport() {
        findNavController().navigate(R.id.action_profile_to_totalReport)
    }

    private fun showResignDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_resign, null)
        val dialogBuilder = AlertDialog.Builder(context, R.style.RoundedDialog)
            .setView(dialogView)
            .create()

        val btnClose = dialogView.findViewById<ImageView>(R.id.btn_close)
        val btnYes = dialogView.findViewById<Button>(R.id.btn_yes)
        val btnNo = dialogView.findViewById<Button>(R.id.btn_no)

        btnClose.setOnClickListener { dialogBuilder.dismiss() }
        btnNo.setOnClickListener { dialogBuilder.dismiss() }
        btnYes.setOnClickListener {
            // 탈퇴
            Toast.makeText(requireContext(), "탈퇴되었습니다!", Toast.LENGTH_SHORT).show()
            dialogBuilder.dismiss()
        }

        dialogBuilder.show()
        setDialogSize(dialogBuilder, 0.9)
    }

    private fun setDialogSize(dialogBuilder: AlertDialog, widthRatio: Double) {
        val window = dialogBuilder.window
        if (window != null) {
            val displayMetrics = DisplayMetrics()
            window.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels

            window.setLayout((width * widthRatio).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }


    private fun checkPermissionAndOpenGallery() {
        // 권한 체크 후 갤러리 열기
        if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )
        }

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(requireContext(), "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getImageExtension(contentResolver: ContentResolver, uri: Uri): String? {
        val mimeType = contentResolver.getType(uri)

        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 1001
        private const val myAccessToken =
            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNzI3NDI1NTk4LCJlbWFpbCI6InRlc3QiLCJleHAiOjE3MzAwMTc1OTh9.tboPAOJ2e7J1D-BVU-RrE0b4eRF7rdkjTBk7MAZlWRA"
    }
}