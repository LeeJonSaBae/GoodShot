package com.ijonsabae.presentation.consult

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.navigation.fragment.navArgs
import androidx.navigation.serialization.UNKNOWN.name
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseDialog
import com.ijonsabae.presentation.databinding.DialogConsultantBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConsultantDetailInfoDialog :
    BaseDialog<DialogConsultantBinding>(DialogConsultantBinding::bind, R.layout.dialog_consultant) {
    private val args: ConsultantDetailInfoDialogArgs by navArgs()
    private val consultantCertificationAdapter by lazy {
        ConsultantCertificationAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initArgs()
//        initRecyclerView()
        initSetOnClickListener()
//        loadProfileImage()
    }

    override fun onStart() {
        super.onStart()
        setScreenWidthPercentage(0.9F)
        setScreenHeightPercentage(0.8F)
    }

    private fun initArgs() {
        args.expertId.apply {
//            binding.tvConsultantName.text = "${name} 프로"
//            binding.tvCareer.text = "총 경력${career}년"
//            binding.tvPhoneNumber.text = phoneNumber
        }
    }

    private fun initSetOnClickListener() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        binding.btnConsult.setOnClickListener {
//            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(args.consult.chatUrl))
//            startActivity(myIntent)
        }
    }

//    private fun initRecyclerView() {
//        binding.rvCertification.adapter = consultantCertificationAdapter
//        consultantCertificationAdapter.submitList(args.consult.certification)
//    }

//    private fun loadProfileImage() {
//        Glide.with(binding.root)
//            .load(args.consult.profileImage)
//            .into(binding.ivProfileImage)
//    }
}