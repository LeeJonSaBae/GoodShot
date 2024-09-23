package com.ijonsabae.presentation.profile

import android.os.Bundle
import android.view.View
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
    }

    private fun showCustomDialog() {
        val customDialog = ChangePasswordDialog()
        customDialog.show(parentFragmentManager, "CustomDialogFragment")
    }

    private fun showTotalReport() {
        findNavController().navigate(R.id.action_profile_to_totalReport)
    }
}