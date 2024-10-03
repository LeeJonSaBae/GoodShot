package com.ijonsabae.presentation.consult

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ijonsabae.domain.model.ExpertDetail
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseDialog
import com.ijonsabae.presentation.databinding.DialogConsultantBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConsultantDetailInfoDialog :
    BaseDialog<DialogConsultantBinding>(DialogConsultantBinding::bind, R.layout.dialog_consultant) {
    private val args: ConsultantDetailInfoDialogArgs by navArgs()
    private val viewModel: ConsultantDetailInfoDialogViewModel by viewModels()
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
        initFlow()
        initArgs()
        initRecyclerView()
        initSetOnClickListener()
    }

    override fun onStart() {
        super.onStart()
        setScreenWidthPercentage(0.9F)
        setScreenHeightPercentage(0.8F)
    }

    private fun initArgs() {
        lifecycleScope.launch(coroutineExceptionHandler) {
            args.expertId.let {
                viewModel.setId(it)
            }
        }
    }

    private fun initSetOnClickListener() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        binding.btnConsult.setOnClickListener {
            lifecycleScope.launch {
                val expertDetail = viewModel.expertDetailInfo.value
                if (expertDetail != ExpertDetail.EMPTY) {
                    val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(expertDetail.counselUrl))
                    startActivity(myIntent)
                } else {
                    showToastShort("상담사 정보를 불러오는 중입니다. 조금만 기다려 주세요!")
                }
            }
        }
    }

    private fun initFlow() {
        lifecycleScope.launch(coroutineExceptionHandler) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.id.collect {
                        val result = viewModel.getExpertDetailInfo(it)
                        val expert = result.getOrThrow().data
                        viewModel.setExpertDetailInfo(expert)
                    }
                }
                launch {
                    viewModel.expertDetailInfo.collect {
                        consultantCertificationAdapter.submitList(it.certificates)
                        setData(it)
                        loadProfileImage(it.imageUrl)
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvCertification.adapter = consultantCertificationAdapter
    }

    private fun setData(expertDetail: ExpertDetail) {
        binding.tvConsultantName.text = "${expertDetail.name} 프로"
        binding.tvCareer.text = "총 경력${expertDetail.expYears}년"
        binding.tvPhoneNumber.text = "010-1234-5678"
    }

    private fun loadProfileImage(url: String) {
        Glide.with(binding.root)
            .load(url)
            .into(binding.ivProfileImage)
    }
}