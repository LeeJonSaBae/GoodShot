package com.ijonsabae.presentation.consult

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ijonsabae.domain.model.Expert
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentConsultBinding
import com.ijonsabae.presentation.main.MainActivity
import com.ijonsabae.presentation.model.convertExpertDetail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "ConsultFragment_싸피"

@AndroidEntryPoint
class ConsultFragment :
    BaseFragment<FragmentConsultBinding>(FragmentConsultBinding::bind, R.layout.fragment_consult) {
    private val consultViewModel: ConsultViewModel by viewModels()
    private val consultantListAdapter by lazy {
        ConsultantListAdapter().apply {
            setItemClickListener(
                object : ConsultantListAdapter.OnItemClickListener {
                    override fun onItemClick(item: Expert) {
                        lifecycleScope.launch(coroutineExceptionHandler) {
                            navController.navigate(
                                ConsultFragmentDirections.actionConsultToConsultDialog(
                                    convertExpertDetail(consultViewModel.getConsultantInfo(item.id).getOrThrow().data)
                                )
                            )
                        }
                    }
                }
            )
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (fragmentContext as MainActivity).showAppBar("전문가 상담")
        initRecyclerView()
        initFlow()
    }

    private fun initFlow() {
        lifecycleScope.launch {
            consultViewModel.consultantList.collectLatest { result ->
                Log.d(TAG, "setConsultantList: $result")
                Log.d(TAG, "initFlow: ${result}")
                consultantListAdapter.submitData(result)
            }
        }
    }

    private fun initRecyclerView() {
        val consultantRecyclerView = binding.rvConsultant
        consultantRecyclerView.layoutManager = LinearLayoutManager(fragmentContext)
        consultantRecyclerView.adapter = consultantListAdapter
    }
}