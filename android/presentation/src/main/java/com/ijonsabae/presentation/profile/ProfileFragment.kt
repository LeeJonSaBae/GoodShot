package com.ijonsabae.presentation.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentProfileBinding
import com.ijonsabae.presentation.main.MainActivity
import com.ijonsabae.presentation.util.makeHeaderByAccessToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "굿샷_ProfileFragment"

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::bind, R.layout.fragment_profile) {

    private val profileViewModel: ProfileViewModel by lazy {
        ViewModelProvider(this)[ProfileViewModel::class.java]
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri: Uri? = data?.data

                if (imageUri != null) {
                    // 선택한 이미지의 URI를 사용하여 크롭 시작
                    startCrop(imageUri)
                } else {
                    Toast.makeText(requireContext(), "이미지를 선택할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val croppedUri = result.uriContent
            binding.ivProfileImg.setImageURI(croppedUri)  // 크롭된 이미지를 ImageView에 설정

            lifecycleScope.launch {
                Log.d(
                    TAG, "presignedURL = ${
                        profileViewModel.getPresignedURL(
                            makeHeaderByAccessToken(myAccessToken), "png"
                        )
                    }"
                )
//                Toast.makeText(
//                    requireContext(), "presignedURL = ${
//                        profileViewModel.getPresignedURL(
//                            makeHeaderByAccessToken(myAccessToken),
//                            "png"
//                        )
//                    }",
//                    Toast.LENGTH_SHORT
//                ).show()
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
                    outputCompressFormat = Bitmap.CompressFormat.PNG // 원하는 옵션 추가
                )
            )
        )
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

    companion object {
        private const val REQUEST_PERMISSION_CODE = 1001
        private const val myAccessToken =
            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqaWh1bkBuYXZlci5jb20iLCJpYXQiOjE3MjczMzc0ODIsImVtYWlsIjoiamlodW5AbmF2ZXIuY29tIiwiZXhwIjoxNzI3MzQxMDgyfQ.6SsJea8yjz35eahmMQ3X46Rs0lGdVo_36pnn2MiABc8"
    }
}