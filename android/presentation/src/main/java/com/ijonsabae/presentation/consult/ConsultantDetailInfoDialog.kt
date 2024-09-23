package com.ijonsabae.presentation.consult

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseDialog
import com.ijonsabae.presentation.databinding.DialogConsultantBinding

class ConsultantDetailInfoDialog :
    BaseDialog<DialogConsultantBinding>(DialogConsultantBinding::bind, R.layout.dialog_consultant) {
    private val args: ConsultantDetailInfoDialogArgs by navArgs()
    private val consultantCertificationAdapter by lazy {
        ConsultantCertificationAdapter()
    }
    private val consultantTopicAdapter by lazy {
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
        initRecyclerView()
        initSetOnClickListener()
        loadProfileImage()
    }

    override fun onStart() {
        super.onStart()
        setScreenWidthPercentage(0.9F)
        setScreenHeightPercentage(0.8F)
    }

    private fun initArgs() {
        args.consult.apply {
            binding.tvConsultantName.text = "${name} 프로"
            binding.tvCareer.text = "총 경력${career}년"
            binding.tvExpertise.text = expertise
        }
    }

    private fun initSetOnClickListener() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun initRecyclerView() {
        binding.rvTopic.adapter = consultantTopicAdapter
        consultantTopicAdapter.submitList(args.consult.topic)
        binding.rvCertification.adapter = consultantCertificationAdapter
        consultantCertificationAdapter.submitList(args.consult.certification)
    }

    private fun loadProfileImage() {
        Glide.with(binding.root)
            .load(args.consult.profileImage)
            .into(binding.ivProfileImage)
    }
}