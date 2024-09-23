package com.ijonsabae.presentation.profile

import android.app.AlertDialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentProfileBinding
import com.ijonsabae.presentation.main.MainActivity

private const val TAG = "굿샷_ProfileFragment"

class ProfileFragment :
    BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::bind, R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (fragmentContext as MainActivity).showAppBar("마이 페이지")

        binding.layoutChangePassword.setOnClickListener {
            showCustomDialog()
        }

        binding.layoutGoTotalReport.setOnClickListener {
            showTotalReport()
        }

        binding.tvResign.setOnClickListener {
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
}