package com.ijonsabae.presentation.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentProfileBinding
import com.ijonsabae.presentation.main.MainActivity

private const val TAG = "굿샷_ProfileFragment"

class ProfileFragment :
    BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::bind, R.layout.fragment_profile) {

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri: Uri? = data?.data
                binding.ivProfileImg.setImageURI(imageUri)
                Toast.makeText(requireContext(), "프로필 사진이 수정되었습니다!", Toast.LENGTH_SHORT).show()
            }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 이상
            if (requireContext().checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    REQUEST_PERMISSION_CODE
                )
            } else {
                openGallery()  // 권한이 있을 때만 갤러리 열기
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6.0 이상, 13 미만
            if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION_CODE
                )
            } else {
                openGallery()  // 권한이 있을 때만 갤러리 열기
            }
        } else {
            openGallery()  // 권한 요청이 필요 없는 경우
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
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
    }
}